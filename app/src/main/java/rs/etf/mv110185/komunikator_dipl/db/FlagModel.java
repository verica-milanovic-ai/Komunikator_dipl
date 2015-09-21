package rs.etf.mv110185.komunikator_dipl.db;

/**
 * Created by Verica Milanovic on 21.09.2015..
 */
public class FlagModel {
    private String name;
    private String value;
    private int id;

    public FlagModel() {
    }

    public FlagModel(String name, String value) {
        this.value = value;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public FlagModel setId(int id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public FlagModel setValue(String value) {
        this.value = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public FlagModel setName(String name) {
        this.name = name;
        return this;
    }
}
