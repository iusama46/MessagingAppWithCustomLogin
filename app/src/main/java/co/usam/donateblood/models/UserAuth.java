package co.usam.donateblood.models;

/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class UserAuth {
    String uId;
    String phone;

    public UserAuth(String uId, String phone) {
        this.uId = uId;
        this.phone = phone;
    }

    public UserAuth() {
    }

    public String getuId() {

        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
