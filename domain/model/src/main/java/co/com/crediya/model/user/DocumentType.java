package co.com.crediya.model.user;

public enum DocumentType {
    CC("CC", "Cédula de Ciudadanía"),
    TI("TI", "Tarjeta de Identidad"),
    CE("CE", "Cédula de Extranjería"),
    PP("PP", "Pasaporte"),
    NIT("NIT", "Número de Identificación Tributaria"),
    RUT("RUT", "Registro Único Tributario");

    private final String code;
    private final String description;

    DocumentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DocumentType fromCode(String code) {
        for (DocumentType docType : DocumentType.values()) {
            if (docType.code.equalsIgnoreCase(code)) {
                return docType;
            }
        }
        throw new IllegalArgumentException("Unknown document type code: " + code);
    }

    public boolean isGovernmentId() {
        return this == CC || this == TI || this == CE || this == PP;
    }

    public boolean isTaxDocument() {
        return this == NIT || this == RUT;
    }
}

