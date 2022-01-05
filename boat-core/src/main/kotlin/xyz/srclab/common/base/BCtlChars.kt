package xyz.srclab.common.base

import xyz.srclab.common.collect.asList

/**
 * Control characters:
 *
 * * BEL (0x07, ^G) beeps;
 * * BS (0x08, ^H) backspaces one column (but not past the beginning of the line);
 * * HT  (0x09,  ^I) goes to the next tab stop or to the end of the line if there is no earlier
 * tab stop;
 * * LF (0x0A, ^J), VT (0x0B, ^K) and FF (0x0C, ^L) all give a linefeed, and if LF/NL (new-line
 * mode) is set also a carriage return;
 * * CR (0x0D, ^M) gives a carriage return;
 * * SO (0x0E, ^N) activates the G1 character set;
 * * SI (0x0F, ^O) activates the G0 character set;
 * * CAN (0x18, ^X) and SUB (0x1A, ^Z) interrupt escape sequences;
 * * ESC (0x1B, ^[) starts an escape sequence;
 * * DEL (0x7F) is ignored;
 * * CSI (0x9B) is equivalent to ESC [.
 */
object BCtlChars {

    const val BEEP: String = "\u0007"

    const val BACKSPACES: String = "\u0008"

    const val GO_NEXT_TAB: String = "\u0009"

    const val LINEFEED: String = "\u000A"

    const val CARRIAGE_RETURN: String = "\u000D"

    const val ACTIVATE_CHARSET_G1: String = "\u000E"

    const val ACTIVATE_CHARSET_G0: String = "\u000F"

    const val INTERRUPT_ESCAPE: String = "\u0018"

    @JvmStatic
    fun escape(value: CharSequence): String {
        return "\u001b$value"
    }
}

/**
 * ESC- but not CSI-sequences:
 *
 * * ESC c     RIS      Reset.
 * * ESC D     IND      Linefeed.
 * * ESC E     NEL      Newline.
 * * ESC H     HTS      Set tab stop at current column.
 * * ESC M     RI       Reverse linefeed.
 * * ESC Z     DECID    DEC private identification. The kernel returns the
 * string  ESC [ ? 6 c, claiming that it is a VT102.
 * * ESC 7     DECSC    Save   current    state    (cursor    coordinates,
 * attributes, character sets pointed at by G0, G1).
 * * ESC 8     DECRC    Restore state most recently saved by ESC 7.
 * * ESC [     CSI      Control sequence introducer
 * * ESC %              Start sequence selecting character set
 *     * ESC % @               Select default (ISO 646 / ISO 8859-1)
 *     * ESC % G               Select UTF-8
 *     * ESC % 8               Select UTF-8 (obsolete)
 * * ESC # 8   DECALN   DEC screen alignment test - fill screen with E's.
 * * ESC (              Start sequence defining G0 character set
 *     * ESC ( B               Select default (ISO 8859-1 mapping)
 *     * ESC ( 0               Select VT100 graphics mapping
 *     * ESC ( U               Select null mapping - straight to character ROM
 *     * ESC ( K               Select user mapping - the map that is loaded by
 * the utility mapscrn(8).
 * * ESC )              Start sequence defining G1
 * (followed by one of B, 0, U, K, as above).
 * * ESC >     DECPNM   Set numeric keypad mode
 * * ESC =     DECPAM   Set application keypad mode
 * * ESC ]     OSC      (Should  be:  Operating  system  command)  ESC ] P
 * nrrggbb: set palette, with parameter  given  in  7
 * hexadecimal  digits after the final P :-(.  Here n
 * is the color  (0–15),  and  rrggbb  indicates  the
 * red/green/blue  values  (0–255).   ESC  ] R: reset
 * palette
 */
object BEscChars {

    @JvmField
    val RESET: String = BCtlChars.escape("c")

    @JvmField
    val LINEFEED: String = BCtlChars.escape("D")

    @JvmField
    val NEWLINE: String = BCtlChars.escape("E")

    @JvmField
    val SET_TAB_AT_CURRENT_COLUMN: String = BCtlChars.escape("H")

    @JvmField
    val REVERSE_LINEFEED: String = BCtlChars.escape("M")

    @JvmField
    val SAVE_STATE: String = BCtlChars.escape("7")

    @JvmField
    val RESTORE_STATE: String = BCtlChars.escape("8")

    @JvmField
    val SELECT_CHARSET_DEFAULT: String = selectCharset("@")

    @JvmField
    val SELECT_CHARSET_UTF8: String = selectCharset("G")

    @JvmField
    val SELECT_CHARSET_UTF8_OBSOLETE: String = selectCharset("8")

    @JvmField
    val FILL_SCREEN_WITH_E: String = BCtlChars.escape("#8")

    @JvmField
    val DEFINE_CHARSET_G0_DEFAULT: String = defineCharsetG0("B")

    @JvmField
    val DEFINE_CHARSET_G0_VT100: String = defineCharsetG0("0")

    @JvmField
    val DEFINE_CHARSET_G0_ROM: String = defineCharsetG0("U")

    @JvmField
    val DEFINE_CHARSET_G0_USER: String = defineCharsetG0("K")

    @JvmField
    val DEFINE_CHARSET_G1_DEFAULT: String = defineCharsetG1("B")

    @JvmField
    val DEFINE_CHARSET_G1_VT100: String = defineCharsetG1("0")

    @JvmField
    val DEFINE_CHARSET_G1_ROM: String = defineCharsetG1("U")

    @JvmField
    val DEFINE_CHARSET_G1_USER: String = defineCharsetG1("K")

    @JvmField
    val SET_KEYPAD_MODE_NUMERIC: String = BCtlChars.escape(">")

    @JvmField
    val SET_KEYPAD_MODE_APPLICATION: String = BCtlChars.escape("=")

    @JvmStatic
    fun csiChars(csiChars: CharSequence): String {
        return BCtlChars.escape("[") + csiChars
    }

    @JvmStatic
    fun selectCharset(charset: CharSequence): String {
        return BCtlChars.escape("%") + charset
    }

    @JvmStatic
    fun defineCharsetG0(charset: CharSequence): String {
        return BCtlChars.escape("(") + charset
    }

    @JvmStatic
    fun defineCharsetG1(charset: CharSequence): String {
        return BCtlChars.escape(")") + charset
    }

    @JvmStatic
    fun osCommand(command: CharSequence): String {
        return BCtlChars.escape("]") + command
    }
}

/**
 * ECMA-48 CSI sequences:
 *
 * CSI (or ESC [) is followed by a sequence of  parameters,  at  most  NPAR  (16),  that  are
 * decimal  numbers  separated by semicolons.  An empty or absent parameter is taken to be 0.
 * The sequence of parameters may be preceded by a single question mark.
 *
 * However, after CSI [ (or ESC [ [) a single character is read and this entire  sequence  is
 * ignored.  (The idea is to ignore an echoed function key.)
 *
 * The action of a CSI sequence is determined by its final character.
 *
 * * @   ICH       Insert the indicated # of blank characters.
 * * A   CUU       Move cursor up the indicated # of rows.
 *
 * * B   CUD       Move cursor down the indicated # of rows.
 * * C   CUF       Move cursor right the indicated # of columns.
 * * D   CUB       Move cursor left the indicated # of columns.
 * * E   CNL       Move cursor down the indicated # of rows, to column 1.
 * * F   CPL       Move cursor up the indicated # of rows, to column 1.
 * * G   CHA       Move cursor to indicated column in current row.
 * * H   CUP       Move cursor to the indicated row, column (origin at 1,1).
 * * J   ED        Erase display (default: from cursor to end of display).
 *     * ESC [ 1 J: erase from start to cursor.
 *     * ESC [ 2 J: erase whole display.
 *     * ESC [ 3 J: erase whole display including scroll-back
 * buffer (since Linux 3.0).
 * * K   EL        Erase line (default: from cursor to end of line).
 *     * ESC [ 1 K: erase from start of line to cursor.
 *     * ESC [ 2 K: erase whole line.
 * * L   IL        Insert the indicated # of blank lines.
 * * M   DL        Delete the indicated # of lines.
 * * P   DCH       Delete the indicated # of characters on current line.
 * * X   ECH       Erase the indicated # of characters on current line.
 * * a   HPR       Move cursor right the indicated # of columns.
 * * c   DA        Answer ESC [ ? 6 c: "I am a VT102".
 * * d   VPA       Move cursor to the indicated row, current column.
 * * e   VPR       Move cursor down the indicated # of rows.
 * * f   HVP       Move cursor to the indicated row, column.
 * * g   TBC       Without parameter: clear tab stop at current position.
 *     * ESC [ 3 g: delete all tab stops.
 * * h   SM        Set Mode (see below).
 * * l   RM        Reset Mode (see below).
 * * m   SGR       Set attributes (see below).
 * * n   DSR       Status report (see below).
 * * q   DECLL     Set keyboard LEDs.
 *     * ESC [ 0 q: clear all LEDs
 *     * ESC [ 1 q: set Scroll Lock LED
 *     * ESC [ 2 q: set Num Lock LED
 *     * ESC [ 3 q: set Caps Lock LED
 * * r   DECSTBM   Set scrolling region; parameters are top and bottom row.
 * * s   ?         Save cursor location.
 * * u   ?         Restore cursor location.
 * * `   HPA       Move cursor to indicated column in current row.
 */
object BCsiChars {

    @JvmField
    val ERASE_DISPLAY_FROM_START_TO_CURSOR: String = eraseDisplay(1)

    @JvmField
    val ERASE_WHOLE_DISPLAY: String = eraseDisplay(2)

    @JvmField
    val ERASE_WHOLE_DISPLAY_INCLUDING_BUFFER: String = eraseDisplay(3)

    @JvmField
    val ERASE_LINE_FROM_START_TO_CURSOR: String = eraseLine(1)

    @JvmField
    val ERASE_WHOLE_LINE: String = eraseLine(2)

    @JvmField
    val DISPLAY_CONTROL_CHARS: String = setMode(3)

    @JvmField
    val SET_INSERT_MODE: String = setMode(4)

    @JvmField
    val FOLLOW_CR: String = setMode(20)

    @JvmField
    val RESET_DISPLAY_CONTROL_CHARS: String = resetMode(3)

    @JvmField
    val RESET_INSERT_MODE: String = resetMode(4)

    @JvmField
    val RESET_FOLLOW_CR: String = resetMode(20)

    @JvmField
    val SAVE_CURSOR: String = BEscChars.csiChars("s")

    @JvmField
    val RESTORE_CURSOR: String = BEscChars.csiChars("u")

    @JvmField
    val REPORT_STATUS: String = BEscChars.csiChars("5n")

    @JvmField
    val REPORT_CURSOR: String = BEscChars.csiChars("6n")

    @JvmStatic
    @JvmOverloads
    fun cursorUp(n: Int = 1): String {
        return BEscChars.csiChars("${n}A")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorDown(n: Int = 1): String {
        return BEscChars.csiChars("${n}B")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorForward(n: Int = 1): String {
        return BEscChars.csiChars("${n}C")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorBack(n: Int = 1): String {
        return BEscChars.csiChars("${n}D")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorNextLine(n: Int = 1): String {
        return BEscChars.csiChars("${n}E")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorPreviousLine(n: Int = 1): String {
        return BEscChars.csiChars("${n}F")
    }

    @JvmStatic
    @JvmOverloads
    fun cursorColumn(n: Int = 1): String {
        return BEscChars.csiChars("${n}G")
    }

    @JvmStatic
    fun cursorMove(n: Int, m: Int): String {
        return BEscChars.csiChars("${n};${m}H")
    }

    @JvmStatic
    @JvmOverloads
    fun scrollUp(n: Int = 1): String {
        return BEscChars.csiChars("${n}S")
    }

    @JvmStatic
    @JvmOverloads
    fun scrollDown(n: Int = 1): String {
        return BEscChars.csiChars("${n}T")
    }

    /**
     * @param n
     * * 1: erase from start to cursor
     * * 2: erase whole display
     * * 3: erase whole display including scroll-back buffer (since Linux 3.0)
     */
    @JvmStatic
    @JvmOverloads
    fun eraseDisplay(n: Int = 2): String {
        return BEscChars.csiChars("${n}J")
    }

    /**
     * @param n
     * * 1: erase from start of line to cursor
     * * 2: erase whole line
     */
    @JvmStatic
    @JvmOverloads
    fun eraseLine(n: Int = 2): String {
        return BEscChars.csiChars("${n}K")
    }

    /**
     * @param h
     * * 3: DECCRM (default off): Display control chars.
     * * 4: DECIM (default off): Set insert mode.
     * * 20: LF/NL (default off): Automatically follow echo of LF, VT or FF with CR.
     */
    @JvmStatic
    fun setMode(h: Int): String {
        return BEscChars.csiChars("${h}h")
    }

    /**
     * @param l
     * * 3: DECCRM (default off): Display control chars.
     * * 4: DECIM (default off): Set insert mode.
     * * 20: LF/NL (default off): Automatically follow echo of LF, VT or FF with CR.
     */
    @JvmStatic
    fun resetMode(l: Int): String {
        return BEscChars.csiChars("${l}l")
    }
}

/**
 * ECMA-48 Set Graphics Rendition:
 *
 * The  ECMA-48  SGR sequence ESC [ parameters m sets display attributes.  Several attributes
 * can be set in the same sequence, separated by semicolons.   An  empty  parameter  (between
 * semicolons or string initiator or terminator) is interpreted as a zero.
 */
object BSgrChars {

    @JvmField
    val RESET: String = BEscChars.csiChars("${BSgrParam.RESET.value}m")

    @JvmOverloads
    @JvmStatic
    fun foregroundBlack(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.FOREGROUND_BRIGHT_BLACK else BSgrParam.FOREGROUND_BLACK)
    }

    @JvmOverloads
    @JvmStatic
    fun foregroundRed(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.FOREGROUND_BRIGHT_RED else BSgrParam.FOREGROUND_RED)
    }

    @JvmOverloads
    @JvmStatic
    fun foregroundGreen(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.FOREGROUND_BRIGHT_GREEN else BSgrParam.FOREGROUND_GREEN)
    }

    @JvmOverloads
    @JvmStatic
    fun foregroundYellow(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.FOREGROUND_BRIGHT_YELLOW else BSgrParam.FOREGROUND_YELLOW)
    }

    @JvmOverloads
    @JvmStatic
    fun foregroundBlue(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.FOREGROUND_BRIGHT_BLUE else BSgrParam.FOREGROUND_BLUE)
    }

    @JvmOverloads
    @JvmStatic
    fun foregroundMagenta(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.FOREGROUND_BRIGHT_MAGENTA else BSgrParam.FOREGROUND_MAGENTA)
    }

    @JvmOverloads
    @JvmStatic
    fun foregroundCyan(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.FOREGROUND_BRIGHT_CYAN else BSgrParam.FOREGROUND_CYAN)
    }

    @JvmOverloads
    @JvmStatic
    fun foregroundWhite(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.FOREGROUND_BRIGHT_WHITE else BSgrParam.FOREGROUND_WHITE)
    }

    @JvmStatic
    fun foregroundDefault(content: Any?): String {
        return withParam(content, BSgrParam.FOREGROUND_DEFAULT)
    }

    @JvmOverloads
    @JvmStatic
    fun backgroundBlack(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.BACKGROUND_BRIGHT_BLACK else BSgrParam.BACKGROUND_BLACK)
    }

    @JvmOverloads
    @JvmStatic
    fun backgroundRed(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.BACKGROUND_BRIGHT_RED else BSgrParam.BACKGROUND_RED)
    }

    @JvmOverloads
    @JvmStatic
    fun backgroundGreen(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.BACKGROUND_BRIGHT_GREEN else BSgrParam.BACKGROUND_GREEN)
    }

    @JvmOverloads
    @JvmStatic
    fun backgroundYellow(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.BACKGROUND_BRIGHT_YELLOW else BSgrParam.BACKGROUND_YELLOW)
    }

    @JvmOverloads
    @JvmStatic
    fun backgroundBlue(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.BACKGROUND_BRIGHT_BLUE else BSgrParam.BACKGROUND_BLUE)
    }

    @JvmOverloads
    @JvmStatic
    fun backgroundMagenta(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.BACKGROUND_BRIGHT_MAGENTA else BSgrParam.BACKGROUND_MAGENTA)
    }

    @JvmOverloads
    @JvmStatic
    fun backgroundCyan(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.BACKGROUND_BRIGHT_CYAN else BSgrParam.BACKGROUND_CYAN)
    }

    @JvmOverloads
    @JvmStatic
    fun backgroundWhite(content: Any?, bright: Boolean = false): String {
        return withParam(content, if (bright) BSgrParam.BACKGROUND_BRIGHT_WHITE else BSgrParam.BACKGROUND_WHITE)
    }

    @JvmStatic
    fun backgroundDefault(content: Any?): String {
        return withParam(content, BSgrParam.BACKGROUND_DEFAULT)
    }

    @JvmStatic
    fun withParam(content: Any?, sgrParams: BSgrParam): String {
        return BEscChars.csiChars("${sgrParams.value}m${content}") + RESET
    }

    @JvmStatic
    fun withParams(content: Any?, vararg sgrParams: BSgrParam): String {
        return withParam(content, BSgrParam.concat(*sgrParams))
    }

    @JvmStatic
    fun withParams(content: Any?, sgrParams: List<BSgrParam>): String {
        return withParam(content, BSgrParam.concat(sgrParams))
    }
}

/**
 * Parameters for [BSgrChars].
 */
interface BSgrParam {

    val value: String

    companion object {

        @JvmField
        val RESET: BSgrParam = of("0")

        @JvmField
        val BOLD: BSgrParam = of("1")

        @JvmField
        val HALF_BRIGHT: BSgrParam = of("2")

        @JvmField
        val ITALIC: BSgrParam = of("3")

        @JvmField
        val UNDERSCORE: BSgrParam = of("4")

        @JvmField
        val BLINK: BSgrParam = of("5")

        @JvmField
        val FAST_BLINK: BSgrParam = of("6")

        @JvmField
        val INVERSE: BSgrParam = of("7")

        @JvmField
        val INVISIBLE: BSgrParam = of("8")

        @JvmField
        val STRIKETHROUGH: BSgrParam = of("9")

        @JvmField
        val PRIMARY_FONT: BSgrParam = of("10")

        @JvmField
        val ALTERNATE_FONT_1: BSgrParam = of("11")

        @JvmField
        val ALTERNATE_FONT_2: BSgrParam = of("12")

        @JvmField
        val ALTERNATE_FONT_3: BSgrParam = of("13")

        @JvmField
        val ALTERNATE_FONT_4: BSgrParam = of("14")

        @JvmField
        val ALTERNATE_FONT_5: BSgrParam = of("15")

        @JvmField
        val ALTERNATE_FONT_6: BSgrParam = of("16")

        @JvmField
        val ALTERNATE_FONT_7: BSgrParam = of("17")

        @JvmField
        val ALTERNATE_FONT_8: BSgrParam = of("18")

        @JvmField
        val ALTERNATE_FONT_9: BSgrParam = of("19")

        @JvmField
        val BOLD_OFF: BSgrParam = of("21")

        @JvmField
        val HALF_BRIGHT_OFF: BSgrParam = of("22")

        @JvmField
        val ITALIC_OFF: BSgrParam = of("23")

        @JvmField
        val UNDERSCORE_OFF: BSgrParam = of("24")

        @JvmField
        val BLINK_OFF: BSgrParam = of("25")

        @JvmField
        val FAST_BLINK_OFF: BSgrParam = of("26")

        @JvmField
        val INVERSE_OFF: BSgrParam = of("27")

        @JvmField
        val INVISIBLE_OFF: BSgrParam = of("28")

        @JvmField
        val STRIKETHROUGH_OFF: BSgrParam = of("29")

        @JvmField
        val FOREGROUND_BLACK: BSgrParam = of("30")

        @JvmField
        val FOREGROUND_RED: BSgrParam = of("31")

        @JvmField
        val FOREGROUND_GREEN: BSgrParam = of("32")

        @JvmField
        val FOREGROUND_YELLOW: BSgrParam = of("33")

        @JvmField
        val FOREGROUND_BLUE: BSgrParam = of("34")

        @JvmField
        val FOREGROUND_MAGENTA: BSgrParam = of("35")

        @JvmField
        val FOREGROUND_CYAN: BSgrParam = of("36")

        @JvmField
        val FOREGROUND_WHITE: BSgrParam = of("37")

        @JvmField
        val FOREGROUND_DEFAULT: BSgrParam = of("39")

        @JvmField
        val BACKGROUND_BLACK: BSgrParam = of("40")

        @JvmField
        val BACKGROUND_RED: BSgrParam = of("41")

        @JvmField
        val BACKGROUND_GREEN: BSgrParam = of("42")

        @JvmField
        val BACKGROUND_YELLOW: BSgrParam = of("43")

        @JvmField
        val BACKGROUND_BLUE: BSgrParam = of("44")

        @JvmField
        val BACKGROUND_MAGENTA: BSgrParam = of("45")

        @JvmField
        val BACKGROUND_CYAN: BSgrParam = of("46")

        @JvmField
        val BACKGROUND_WHITE: BSgrParam = of("47")

        @JvmField
        val BACKGROUND_DEFAULT: BSgrParam = of("49")

        @JvmField
        val FRAMED: BSgrParam = of("51")

        @JvmField
        val ENCIRCLED: BSgrParam = of("52")

        @JvmField
        val OVERLINE: BSgrParam = of("53")

        @JvmField
        val FRAMED_ENCIRCLED_OFF: BSgrParam = of("54")

        @JvmField
        val OVERLINE_OFF: BSgrParam = of("55")

        @JvmField
        val FOREGROUND_BRIGHT_BLACK: BSgrParam = of("90")

        @JvmField
        val FOREGROUND_BRIGHT_RED: BSgrParam = of("91")

        @JvmField
        val FOREGROUND_BRIGHT_GREEN: BSgrParam = of("92")

        @JvmField
        val FOREGROUND_BRIGHT_YELLOW: BSgrParam = of("93")

        @JvmField
        val FOREGROUND_BRIGHT_BLUE: BSgrParam = of("94")

        @JvmField
        val FOREGROUND_BRIGHT_MAGENTA: BSgrParam = of("95")

        @JvmField
        val FOREGROUND_BRIGHT_CYAN: BSgrParam = of("96")

        @JvmField
        val FOREGROUND_BRIGHT_WHITE: BSgrParam = of("97")

        @JvmField
        val BACKGROUND_BRIGHT_BLACK: BSgrParam = of("100")

        @JvmField
        val BACKGROUND_BRIGHT_RED: BSgrParam = of("101")

        @JvmField
        val BACKGROUND_BRIGHT_GREEN: BSgrParam = of("102")

        @JvmField
        val BACKGROUND_BRIGHT_YELLOW: BSgrParam = of("103")

        @JvmField
        val BACKGROUND_BRIGHT_BLUE: BSgrParam = of("104")

        @JvmField
        val BACKGROUND_BRIGHT_MAGENTA: BSgrParam = of("105")

        @JvmField
        val BACKGROUND_BRIGHT_CYAN: BSgrParam = of("106")

        @JvmField
        val BACKGROUND_BRIGHT_WHITE: BSgrParam = of("107")

        @JvmStatic
        fun of(value: CharSequence): BSgrParam {
            return SgrParamImpl(value.toString())
        }

        @JvmStatic
        fun concat(vararg params: BSgrParam): BSgrParam {
            return concat(params.asList())
        }

        @JvmStatic
        fun concat(params: List<BSgrParam>): BSgrParam {
            return SgrParamImpl(params.joinToString(separator = ";") { r -> r.value })
        }

        @JvmStatic
        fun alternateFont(n: Int): BSgrParam {
            return when (n) {
                1 -> ALTERNATE_FONT_1
                2 -> ALTERNATE_FONT_2
                3 -> ALTERNATE_FONT_3
                4 -> ALTERNATE_FONT_4
                5 -> ALTERNATE_FONT_5
                6 -> ALTERNATE_FONT_6
                7 -> ALTERNATE_FONT_7
                8 -> ALTERNATE_FONT_8
                9 -> ALTERNATE_FONT_9
                else -> throw IllegalArgumentException("Number of Alternate Font should be in 1..10.")
            }
        }

        @JvmStatic
        fun foregroundColor(n: Int): BSgrParam {
            return SgrParamImpl("38;5;$n")
        }

        @JvmStatic
        fun foregroundColor(r: Int, g: Int, b: Int): BSgrParam {
            return SgrParamImpl("38;2;$r;$g;$b")
        }

        @JvmStatic
        fun backgroundColor(n: Int): BSgrParam {
            return SgrParamImpl("48;5;$n")
        }

        @JvmStatic
        fun backgroundColor(r: Int, g: Int, b: Int): BSgrParam {
            return SgrParamImpl("48;2;$r;$g;$b")
        }

        private class SgrParamImpl(override val value: String) : BSgrParam
    }
}