package cn.dextea.store.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NearbyStore {
    private Long id;
    private Double distance;
    private String distanceUnit;

    public Double getDistance() {
        if(distance<1){
            DecimalFormat df = new DecimalFormat("#");
            return Double.valueOf(df.format(distance*1000));
        }else{
            DecimalFormat df = new DecimalFormat("#.00");
            return Double.valueOf(df.format(distance));
        }
    }

    public String getDistanceUnit() {
        if (distance < 1) {
            return "m";
        } else {
            return "km";
        }
    }

}
