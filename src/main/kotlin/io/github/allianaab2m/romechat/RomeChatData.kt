package io.github.allianaab2m.romechat

data class RomeChatData (var mode: RomeChatMode) {
    enum class RomeChatMode {
        ON,
        BRACKET_OFF,
        OFF;
        fun toggle(): RomeChatMode {
            return when (this) {
                ON, BRACKET_OFF -> OFF
                OFF -> ON
            }
        }

        fun cycle(): RomeChatMode {
            return when (this) {
                ON -> BRACKET_OFF
                BRACKET_OFF -> OFF
                OFF -> ON
            }
        }
    }

    fun toggleMode() {
        this.mode = this.mode.toggle()
    }

    fun cycleMode() {
        this.mode = this.mode.cycle()
    }
}
