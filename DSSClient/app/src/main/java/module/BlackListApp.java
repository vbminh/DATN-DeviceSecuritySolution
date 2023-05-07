package module;

import java.io.Serializable;

public class BlackListApp implements Serializable {
    String name ;
    String id;
    String imageUrl;
    String category;
    String version;
    String os;
    String owner;

    public BlackListApp(String name, String id, String imageUrl, String category, String version,String os, String owner ) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
        this.category = category;
        this.version = version;
        this.os = os;
        this.owner = owner;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}


