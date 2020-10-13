package co.usam.donateblood.models;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class User {
    String cnic;
    String blood_group;
    String name;
    String age;
    String user_id;
    String phone_Num;
    boolean is_verified;
    boolean is_exists;
    String img_url1,img_url2;

    public User(String cnic, String blood_group, String name, String age, String user_id, String phone_Num, boolean is_verified, boolean is_exists, String img_url1, String img_url2) {
        this.cnic = cnic;
        this.blood_group = blood_group;
        this.name = name;
        this.age = age;
        this.user_id = user_id;
        this.phone_Num = phone_Num;
        this.is_verified = is_verified;
        this.is_exists = is_exists;
        this.img_url1 = img_url1;
        this.img_url2 = img_url2;
    }

    public User() {
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_Num() {
        return phone_Num;
    }

    public void setPhone_Num(String phone_Num) {
        this.phone_Num = phone_Num;
    }

    public boolean isIs_verified() {
        return is_verified;
    }

    public void setIs_verified(boolean is_verified) {
        this.is_verified = is_verified;
    }

    public boolean isIs_exists() {
        return is_exists;
    }

    public void setIs_exists(boolean is_exists) {
        this.is_exists = is_exists;
    }

    public String getImg_url1() {
        return img_url1;
    }

    public void setImg_url1(String img_url1) {
        this.img_url1 = img_url1;
    }

    public String getImg_url2() {
        return img_url2;
    }

    public void setImg_url2(String img_url2) {
        this.img_url2 = img_url2;
    }
}
