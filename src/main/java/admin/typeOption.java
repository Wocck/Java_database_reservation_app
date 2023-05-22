package admin;

/**
 * Definition of the types of equipment available in the classroom
 */
public enum typeOption {
    computer, printer, projector, speakers, tv, other;
    private typeOption(){}

    public String value(){
        return name();
    }

    public static typeOption fromValue(String v){
        return valueOf(v);
    }
}
