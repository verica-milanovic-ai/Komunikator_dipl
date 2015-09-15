package rs.etf.mv110185.komunikator_dipl.db;

/**
 * Created by Verica Milanovic  on 8/2/2015.
 */
public class OptionModel {
    private int id;
    private String image_src;
    private int is_sub_option;
    private int is_final;
    private int parent;
    private String text;
    private String final_text;

    public OptionModel(){}

    public OptionModel(String image_src, int is_sub_option, int is_final, int parent, String text, String final_text) {
        this.image_src = image_src;
        this.is_sub_option = is_sub_option;
        this.is_final = is_final;
        this.parent = parent;
        this.text = text;
        this.final_text = final_text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_src() {
        return image_src;
    }

    public void setImage_src(String image_src) {
        this.image_src = image_src;
    }

    public int getIs_sub_option() {
        return is_sub_option;
    }

    public void setIs_sub_option(int is_sub_option) {
        this.is_sub_option = is_sub_option;
    }

    public int getIs_final() {
        return is_final;
    }

    public void setIs_final(int is_final) {
        this.is_final = is_final;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFinal_text() {
        return final_text;
    }

    public void setFinal_text(String final_text) {
        this.final_text = final_text;
    }

    @Override
    public String toString() {
        return "Option[" +
                "id=" + id +
                ", image_src='" + image_src + '\'' +
                ", is_sub_option=" + is_sub_option +
                ", is_final=" + is_final +
                ", parent=" + parent +
                ", text='" + text + '\'' +
                ", final_text='" + final_text + '\'' +
                ']';
    }
}
