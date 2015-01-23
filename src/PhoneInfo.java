import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/1/24.
 */
public class PhoneInfo {
    String overView;
    List<String> imgs;
    Map<String,List<PhoneSpec>> phoneSpeces;

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public Map<String, List<PhoneSpec>> getPhoneSpeces() {
        return phoneSpeces;
    }

    public void setPhoneSpeces(Map<String, List<PhoneSpec>> phoneSpeces) {
        this.phoneSpeces = phoneSpeces;
    }
}
