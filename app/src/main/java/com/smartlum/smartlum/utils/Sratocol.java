package com.smartlum.smartlum.utils;

import android.graphics.Color;
import android.util.Log;

import java.util.Arrays;

/**
 * <p>
 * {@code Sratocol} - класс (протокол), который представляет собой ранний (и на данный момент единственный) вариант общения с устройством SL-EASY.
 * {@code Sratocol} включает методы для создания и чтения пакетов. Разработан для тех, кто тупой или не хочет разбираться с BLE.
 * Пакет (массив) содержит в себе 9 информационных байтов и выглядит это чудовище следующим образом:
 * <pre>
 *     [0] - Адрес - кому он адресован.
 *           По факту приложению похер на этот байт,
 *           он сущетвует для внутренней логики переферийного устройства.
 *           Мы всегда передаем одно значение.
 *           Но если тебе скучно и ты хочешь порофлить над тем,
 *           как твои пакеты залетают напрямую, например, к флешке устройства
 *           и тем самым устроить попаболь своему коллеге...
 *     [1] - Адрес - от кого.
 *           Приложению тоже похер на этот байт
 *           если оно поймало этот пакет - значит так нужно.
 *     [2] - Тип комманды. Они описаны здесь {@link Sratocol.Command}
 *     [3] - Регистр. Они описаны здесь {@link Sratocol.Register}
 *     [4] - DataCount. Ебучий костыль, от которого я просил избавиться.
 *           Если в нем лежит "1" - значит Value разделено на 5 и 6 байты.
 *           Если "0" - значит Value лежит только в 5 байте.
 *           Дело в том, что в один байт влезает только 0-255.
 *           Если больше, то нам нужно разделить данные на два байта.
 *     [5] - Data1. Содержит в себе передаваемое значение.
 *     [6] - Data2, если Data1 не хватило для передачи значения.
 *           CRC1, если значение влезло в Data1.
 *     [7] - CRC1 или CRC2 по такой же логике как и выше.
 *     [8] - CRC2 или ПУСТОЙ БАЙТ. Да-да, мы можем передавать никому не всравшийся байт.
 *           Почему же нельзя было просто оставить в покое Data2 и не заниматься хуйней?
 *           Спросите об этом Айдара, он нассыт вам в уши почему нельзя.
 *           Вот кстати его контакты https://vk.com/bikair.
 * </pre>
 *
 * <p>
 * {@link #createPacket(byte command, byte register, int value)} - создает ДЕФОЛТНЫЙ пакет (массив из 9 байтов), который можно отправить переферийному устройству.
 * </p>
 *
 * <p>
 * {@link #unpackPacket(byte[] rawData)} - распаковывает "сырой" массив с даннами, извлекая из него тип коМанды, регистр и значение.
 * </p>
 *
 * Пример создания пакета:
 * <pre>
 *     byte[] data = Sratocol.createPacket(Sratocol.Command.WRITE,
 *                                         Sratocol.Register.ANIMATION_MODE,
 *                                         Sratocol.Animation.ANIMATION_1);
 *     bluetoothDevice.writeData(data);
 * </pre>
 * Этот пример ЗАПИШЕТ в регистр ANIMATION_MODE значение ANIMATION_1.
 * Таким же образом можно оправить команду Sratocol.Command.READ в данный регистр и получить значение из него (третий аргумент можно игнорировать)
 *
 * <br>
 * <br>
 * Пример распаковки:
 * <pre>
    public void receive(byte[] data) {
        int[] packet = Sratocol.unpackPacket(data);
           if (packet != null) {
               switch (packet[0]) {
                   case Protocol.Command.WRITE:
                   ...
                   break;

                   case Protocol.Command.READ:
                   ...
                   break;

                   case Protocol.Command.RGB:
                   // Сюрприз, отдельная команда для записи RGB пакета!
                   // Почему? Потому что иди нахуй,
                   // у нас всего 2 байта для Value в дефолтном пакете
                   // Мы заменяем байт DataCount на Blue канал и хоба! Все влезает
                   break;

                   case Protocol.Command.END_OF_SETTINGS:
                   ...
                   break;

                   case Protocol.Command.ERROR:
                   ...
                   break;
               }
        }
    }
 * </pre>
 * <p>
 *     Что еще нужно знать.
 *     Главное это то, что типы данных Byte на перефирийном устройстве и в приложении отличаются.
 *     В Java byte имеет границы -128..127, но на переферийном устройстве это 0..255.
 *     Соответсвенно это все нужно правильно конвертировать (смотри методы {@link #unsignedToBytes(byte)} и {@link #unsignedToBytes(byte[])}).
 *     Это делается автоматически при создании и распаковке,
 *     так что можно не париться по этому поводу.
 *     Как ты мог заметить, тут используется 16-битное CRC для проверки корректности получаемых данных.
 *     Если пакет битый, ты узнаешь об этом в логах.
 * </p>
 *
 * @author  Timur Yumalin
 * @author  Aydar Biktimerov
 * @author  Nursultan Salikhov
 */

public class Sratocol {
    private static final String TAG = "Sratocol";
    private static final byte[] packet = new byte[9]; // Размер пакета в 9 байтов
    private static int CRC16;

    /**
     * По идее можно сделать нормальный конструктор,
     * в котором можно задавать размер пакета и его вид.
     * Но т.к. нам это не понадобилось - все сделано в лоб.
     * Но чтобы не создавать экземпляр протокола, я сделал его статическим.
     */
    private Sratocol() {
        // Пустой конструктор
    }

    /**
     *
     * @param command Тип комманды, подробнее смотри в Sratocol.Command
     * @param register Регистр, подробнее смотри в Sratocol.Register
     * @param value Передаваемое значение
     * @return Массив байтов (пакет), который непосредственно передается устройству
     */
    public static byte[] createPacket(byte command, byte register, int value) {
        packet[Index.DATA_RECEIVER]    = (byte) 0xB3; // CONSTANT Data for  [Nordic]
        packet[Index.DATA_TRANSMITTER] = (byte) 0xB6; // CONSTANT Data from [app]
        packet[Index.COMMAND]  = command;
        packet[Index.REGISTER] = register;
        // Здесь мы и делаем проверку размера байтов, если больше 255, то разбиваем на 2 байта
        if (value > 255) {
            packet[Index.B.DATA_COUNT] = 2;
            packet[Index.B.DATA_1]     = (byte) (value >> 8);    // Data 1
            packet[Index.B.DATA_2]     = (byte) (value & 0xFF);  // Data 2
            CRC16 = createCRC(packet);
            packet[Index.B.CRC_1]      = (byte) (CRC16>>8);      // CRC 1
            packet[Index.B.CRC_2]      = (byte) (CRC16 & 0xFF);  // CRC 2
        } else {
            packet[Index.S.DATA_COUNT] = 1;
            packet[Index.S.DATA_1]     = (byte) value;
            CRC16 = createCRC(packet);
            packet[Index.S.CRC_1]      = (byte) (CRC16>>8);      // CRC 1
            packet[Index.S.CRC_2]      = (byte) (CRC16 & 0xFF);  // CRC 2
            packet[Index.S.EMPTY_BYTE] = 0;
        }
        return packet;
    }

    /**
     *
     * @param packet Raw-массив из 9 байтов, получаемых с переферийного устройства.
     * @return Целочисленный массив в котором:
     *     <br>
     *     [0] - Тип команды {@link Sratocol.Command}
     *     <br>
     *     [1] - Регистр {@link Sratocol.Register}
     *     <br>
     *     [2] - Значение {@link Integer}
     */
    public static int[] unpackPacket(byte[] packet) {
        // Incoming packet bytes has C uint_8 type [0...255]
        // Java bytes range is [-128...127]
        // We need to convert it by using unsignedToBytes function
        int[] converted = unsignedToBytes(packet);

        // If packet is RGB
        if (packet[2] == Command.RGB) {
            return unpackRGBPacket(packet);
        }
        // if packet has 2 data bytes
        if (unsignedToBytes(packet[Index.B.DATA_COUNT]) == 2) {
            int crcRx = unsignedToBytes(packet[7]) << 8;
            crcRx |= unsignedToBytes(packet[8]);
            // Checking CRC for packet integrity
            if (crcRx == createCRC(packet)) {
                int[] mPacket = new int[3];
                mPacket[0] = converted[Index.COMMAND];
                mPacket[1] = converted[Index.REGISTER];
                mPacket[2] = converted[Index.B.DATA_1] << 8;
                mPacket[2] |= converted[Index.B.DATA_2];
                return mPacket;
            } else {
                Log.e(TAG, "CRC check FAILED, packet: " + Arrays.toString(converted));
                return null;
            }

        // if packet has 1 data byte
        } else {
            int crcRx = unsignedToBytes(packet[6]) << 8;
            crcRx |= unsignedToBytes(packet[7]);
            // Checking CRC for packet integrity
            if (crcRx == createCRC(packet)) {
                int[] mPacket = new int[3];
                mPacket[0] = converted[Index.COMMAND];
                mPacket[1] = converted[Index.REGISTER];
                mPacket[2] = converted[Index.S.DATA_1];
                return mPacket;
            } else {
                Log.e(TAG, "CRC check FAILED, packet: " + Arrays.toString(converted));
                return null;
            }
        }
    }

    /**
     *
     * @param value Цвет, в теле метода разбивается на RGB каналы.
     *              Почему нельзя передавать через команду Sratocol.Command.WRITE?
     *              Потому-что 3 канала цвета RGB не влезут в Data1 и Data2.
     *              Поэтому и был создан этот костыль путем замены DataCount на Blue канал.
     * @return Такой же пакет, как и обычно, но вместо DataCount байта лежит Blue канал.
     */
    public static byte[] createRGBPacket(int value) {
        packet[Index.DATA_RECEIVER]    = (byte) 0xB3; // CONSTANT Data for  [Nordic]
        packet[Index.DATA_TRANSMITTER] = (byte) 0xB6; // CONSTANT Data from [app]
        packet[Index.COMMAND]       = Command.RGB;
        packet[Index.REGISTER]      = (byte) Color.red(value);
        packet[Index.DATA_COUNT]    = 2;
        packet[Index.B.DATA_1]      = (byte) Color.green(value);
        packet[Index.B.DATA_2]      = (byte) Color.blue(value);
        CRC16 = createCRC(packet);
        packet[Index.B.CRC_1]       = (byte) (CRC16>>8);
        packet[Index.B.CRC_2]       = (byte) (CRC16 & 0xFF);

        return packet;
    }

    /**
     *
     * @param packet Принятый RGB пакет.
     *               Этот метод не нужно вызывать вручную.
     *               Он автоматически сработает при распаковке пакета через {@link #unpackPacket(byte[] data)}
     * @return Массив из 2 элементов (Команда RGB, и цвет в формате Integer)
     */
    private static int[] unpackRGBPacket(byte[] packet) {
        int[] converted = unsignedToBytes(packet);
        int crcRx = unsignedToBytes(packet[7]) << 8;
        crcRx |= unsignedToBytes(packet[8]);
        if (crcRx == createCRC(packet)) {
            int[] mPacket = new int[2];
            mPacket[0]  = converted[Index.COMMAND];
            int red     = converted[Index.REGISTER];
            int green   = converted[Index.B.DATA_1];
            int blue    = converted[Index.B.DATA_2];
            int rgb     = Color.rgb(red,green,blue);
            mPacket[1]  = rgb; // Color
            return mPacket;
        } else {
            Log.e(TAG, "CRC check FAILED, packet: " + Arrays.toString(converted));
            return null;
        }
    }

    /**
     * Этот метод используется для создания пакета сброса до заводских настроек.
     * @return Пакет, который необходимо передать для сброса.
     */
    public static byte[] resetDevice() {
        packet[Index.DATA_RECEIVER]    = (byte) 0xB3; // CONSTANT Data for  [Nordic]
        packet[Index.DATA_TRANSMITTER] = (byte) 0xB6; // CONSTANT Data from [app]
        packet[Index.COMMAND]      = Command.HARD_RESET;
        packet[Index.REGISTER]     = Register.NULL;
        packet[Index.S.DATA_COUNT] = 1;
        packet[Index.S.DATA_1]     = (byte) 1;
        CRC16 = createCRC(packet);
        packet[Index.S.CRC_1]      = (byte) (CRC16>>8);
        packet[Index.S.CRC_2]      = (byte) (CRC16 & 0xFF);
        packet[Index.S.EMPTY_BYTE] = 0;
        return packet;
    }

    /**
     * Этот метод служит для создания/проверки CRC.
     * Его не нужно вызывать, он срабатывает автоматически при распаковке и создании пакетов.
     * CRC - это 2 последних байта в пакете (в один байт не влезает).
     * Значение CRC зависит от значения остальных байтов и заранее заданного числа внутри этого метода.
     * Принимающая сторона получает CRC и должна обратным способом получить то же самое заранее заданное число.
     * Если число получено - значит с пакетом все хорошо.
     * Если число не сходится - значит это битый пакет (помеха или еще что-нибудь).
     * @param buffer Пакет, на основе которого будет создано CRC
     * @return Число (CRC), которое помещается в конец пакета.
     */
    private static int createCRC(final byte[] buffer) {
        int crc = 0xFFFF;
        if (unsignedToBytes(buffer[4]) == 2) {
            for (int j = 0; j < buffer.length-2 ; j++) {
                crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
                crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
                crc ^= ((crc & 0xff) >> 4);
                crc ^= (crc << 12) & 0xffff;
                crc ^= ((crc & 0xFF) << 5) & 0xffff;
            }
            crc &= 0xffff;
            return crc;
        }

        else if (unsignedToBytes(buffer[4]) == 1) {
            for (int j = 0; j < buffer.length-3 ; j++) {
                crc = ((crc  >>> 8) | (crc  << 8) )& 0xffff;
                crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
                crc ^= ((crc & 0xff) >> 4);
                crc ^= (crc << 12) & 0xffff;
                crc ^= ((crc & 0xFF) << 5) & 0xffff;
            }
            crc &= 0xffff;
            return crc;
        }
        else {
            return 0;
        }
    }

    /**
     * Метод, который переводит UInt8 в byte.
     * Принимаемые от устройства байты имеют диапазон 0..255.
     * У нас же байты лежать в диапазоне -128..127.
     * С помощью этого метода мы синхронизируем тип данных.
     * @param b Число, которое будет конвертировано
     * @return int, почему Integer? А кто использует byte в приложении?
     */
    private static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    /**
     * Аналог своего метода-брата {@link #unsignedToBytes(byte)},
     * только принимает в себя массив.
     * @param b Тот самый массив, который будет конвертирован.
     * @return Сконвертированный пакет данных в формате int.
     */
    public static int[] unsignedToBytes(byte[] b) {
        int[] array = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            array[i] = b[i] & 0xFF;
        }
        return array;
    }

    /**
     * Содержит в себе команды, необходимые для общения с устройством.
     * Реализация не самая элегантная, но мне похуй.
     */
    public static class Command {

        public static final byte REQUEST_DISCONNECT   = 0x00;
        public static final byte READ                 = 0x01;
        public static final byte WRITE                = 0x02;
        public static final byte ERROR                = 0x03;
        public static final byte GET_SETTINGS         = 0x04;
        public static final byte RGB                  = 0x05;
        public static final byte HARD_RESET           = 0x10;
        public static final byte END_OF_SETTINGS      = 0x11;
    }

    /**
     * Содержит в себе регистры, к которым мы хотим (или не хотим) обратиться.
     * Реализация не самая элегантная, но мне похуй.
     */
    public static class Register {

        public static final byte NULL                       = 0;
        public static final byte SETTINGS_FLAG              = 1;
        public static final byte STAIRS_BRIGHTNESS          = 2;
        public static final byte ANIMATION_MODE             = 3;
        public static final byte BACKLIGHT_ON_DELAY         = 4;
        public static final byte BOT_SENS_DIRECTION         = 5;
        public static final byte TOP_SENS_DIRECTION         = 6;
        public static final byte BOT_SENS_TRIGGER_DISTANCE  = 7;
        public static final byte TOP_SENS_TRIGGER_DISTANCE  = 8;
        public static final byte RANDOM_COLOR_MODE          = 12;
        public static final byte STEPS_COUNT                = 13;
        public static final byte STRIP_TYPE                 = 15;
        public static final byte SENS_TYPE                  = 16;
        public static final byte CLOCK                      = 17;
        public static final byte ANIMATION_ON_SPEED         = 19;
        public static final byte ANIMATION_OFF_SPEED        = 20;
        public static final byte BRIGHTNESS_MODE            = 21;
        public static final byte ALARM_A                    = 23;
        public static final byte ALARM_B                    = 24;
        public static final byte DAY_NIGHT_MODE             = 25;
        public static final byte BOT_SENS_CURRENT_LIGHTNESS = 28;
        public static final byte BOT_SENS_CURRENT_DISTANCE  = 29;
        public static final byte TOP_SENS_CURRENT_LIGHTNESS = 31;
        public static final byte TOP_SENS_CURRENT_DISTANCE  = 32;

        // Эти регистры мы не используем, с ними работает само устройство,
        // но, как я говорил выше, если мы хотим потроллить этих додиков сишников,
        // то можешь обращаться к ним
        public static final byte RED                  = 9;
        public static final byte GREEN                = 10;
        public static final byte BLUE                 = 11;
        public static final byte STAIRS_OFF_DELAY     = 14;
        public static final byte SERIAL_TYPE          = 0x0C;
        public static final byte SERIAL_CAT           = 0x0D;
        public static final byte SERIAL_BLOCK         = 0x0E;
        public static final byte NUMBER_1             = 0x0F;
        public static final byte NUMBER_2             = 0x10;
        public static final byte NUMBER_3             = 0x11;
        public static final byte RESERVED14           = 0x12;
        public static final byte RESERVED15           = 0x13;

    }

    /**
     * Содержит в себе названия и индексы байтов в пакете (массиве).
     * Сделано для удобство (насколько это возможно), чтобы не нужно было хардкодить.
     * Не забываем что есть ебучий сдвиг из-за Data1/Data2.
     */
    public static class Index {

        public static final byte DATA_RECEIVER     = 0;
        public static final byte DATA_TRANSMITTER  = 1;
        public static final byte COMMAND           = 2;
        public static final byte REGISTER          = 3;
        public static final byte DATA_COUNT        = 4;
        public static final byte DATA_1            = 5;

        // Покет
        public static class B {

            public static final byte DATA_COUNT    = 4;
            public static final byte DATA_1        = 5;
            public static final byte DATA_2        = 6;
            public static final byte CRC_1         = 7;
            public static final byte CRC_2         = 8;
        }

        // Покет по менбше
        public static class S {

            public static final byte DATA_COUNT    = 4;
            public static final byte DATA_1        = 5;
            public static final byte CRC_1         = 6;
            public static final byte CRC_2         = 7;
            public static final byte EMPTY_BYTE    = 8;
        }
    }

    /**
     * Хранит названия анимаций и их значения для передачи
     * Такое же хотелось сотворить для всего остального,
     * но мне лень
     */
    public static class Animation {

        public static final byte ANIMATION_1    = 1;
        public static final byte ANIMATION_2    = 2;
        public static final byte ANIMATION_3    = 3;
        public static final byte ANIMATION_4    = 4;
        public static final byte ANIMATION_5    = 5;
        public static final byte ANIMATION_6    = 6;

    }

}
