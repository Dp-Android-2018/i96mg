package m.dp.i96mg.service.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ErrorResponse {

    @SerializedName("errors")
    private  ArrayList<String>  errors;


    public void setErrors( ArrayList<String>  errors) {
        this.errors = errors;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }
}
