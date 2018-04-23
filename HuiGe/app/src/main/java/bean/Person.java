package bean;
import cn.bmob.v3.BmobObject;

public class Person extends BmobObject {
    public String Name;
    public String Phonenumber;
    public String Yewu;
    public long Time;
    public boolean IsOK=false;

    public boolean getIsOK() {
        return IsOK;
    }

    public void setIsOK(boolean OK) {
        IsOK = OK;
    }
    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public String getYewu() {
        return Yewu;
    }

    public void setYewu(String yewu) {
        Yewu = yewu;
    }
}
