package gr.indahouse.utils;

public class Extras {
    private String extraId, extraName, extraPrice;

    public Extras() {
    }

    public Extras(String extraId, String extraName, String extraPrice) {
        this.extraId = extraId;
        this.extraName = extraName;
        this.extraPrice = extraPrice;
    }

    public String getExtraId() {
        return extraId;
    }

    public void setExtraId(String extraId) {
        this.extraId = extraId;
    }

    public String getExtraName() {
        return extraName;
    }

    public void setExtraName(String extraName) {
        this.extraName = extraName;
    }

    public String getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(String extraPrice) {
        this.extraPrice = extraPrice;
    }
}
