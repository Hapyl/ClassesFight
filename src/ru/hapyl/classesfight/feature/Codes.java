package ru.hapyl.classesfight.feature;

public enum Codes {

    STARTED(new RedeemableCode("Started Code", new Code("STARTERCODE0"), -1L));

    private final RedeemableCode code;

    Codes(RedeemableCode code) {
        this.code = code;
    }

    public RedeemableCode getCode() {
        return code;
    }


}
