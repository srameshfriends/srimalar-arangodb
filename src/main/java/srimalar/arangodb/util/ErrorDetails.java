package srimalar.arangodb.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.springframework.http.HttpStatus;

@JsonRootName("error")
public class ErrorDetails {
    private String id, code, title, detail;
    private HttpStatus status;

    @JsonIgnore
    private Object[] arguments;

    public ErrorDetails() {
        this.id = "100";
        status = HttpStatus.BAD_REQUEST;
        code = "";
        title = "";
        detail = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
