package co.usam.donateblood.constants;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class Constants {
    public static final String CNIC_IMAGES = "cnic_images";
    public static final String USERS = "users";
    public static final String phone="phone";
    public static final String PHONE_NUMBER="phone_num";
    public static final String uid="uid";
    public static final String APPROVAL ="Approval";
    public static final String ROLE="role";
    public static final String BLOOD_BANK="blood_banks";

    public enum BloodGroup {
        OPositive,
        APositive,
        BPositive,
        ABPositive,
        ONegative,
        ANegative,
        BNegative,
        ABNegative,
    }

    public enum Gender {
        FeMale,
        Male,
        Others,
    }

    public enum Role {
        users,
        blood_banks
    }
}



