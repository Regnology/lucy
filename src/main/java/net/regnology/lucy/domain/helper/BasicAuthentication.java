package net.regnology.lucy.domain.helper;

import java.io.Serializable;

public class BasicAuthentication implements Serializable {

    private String username;
    private String password;

    public BasicAuthentication() {}

    public BasicAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BasicAuthentication{" +
            "username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
