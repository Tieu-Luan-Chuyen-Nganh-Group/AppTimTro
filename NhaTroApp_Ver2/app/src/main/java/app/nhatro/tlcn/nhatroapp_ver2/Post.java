package app.nhatro.tlcn.nhatroapp_ver2;

public class Post {

    public String uid, time, date, status, profileimage, postimage, fullname, description, phone;



    public Post(){}

    public Post(String uid, String time, String date, String profileimage, String postimage, String fullname, String description) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.profileimage = profileimage;
        this.postimage = postimage;
        this.fullname = fullname;
        this.description = description;
    }

    public Post(String uid, String time, String date, String status, String profileimage, String postimage, String fullname, String description) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.status = status;
        this.profileimage = profileimage;
        this.postimage = postimage;
        this.fullname = fullname;
        this.description = description;
    }

    public Post(String uid, String time, String date, String status, String profileimage, String postimage, String fullname, String description, String phone) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.status = status;
        this.profileimage = profileimage;
        this.postimage = postimage;
        this.fullname = fullname;
        this.description = description;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
