package com.example.demo.Img;

import org.hibernate.validator.constraints.NotBlank;

public class AdPasterGifReq {

    public static final String LEFT_UP = "left_up";

    public static final String RIGHT_UP = "right_up";

    public static final String LEFT_DOWN = "left_down";

    public static final String RIGHT_DOWN = "right_down";


    @NotBlank(message = "广告水印选择不能为空")
    private String isNeedWatermark;

    private String watermarkPosition;

    /*@ApiModelProperty(value = "截取图片左上角的x坐标")
    @Min(value = 0)
    @NotNull(message = "坐标x不能为空")
    private Integer x;

    @ApiModelProperty(value = "截取图片左上角的y坐标")
    @Min(value = 0)
    @NotNull(message = "坐标y不能为空")
    private Integer y;

    @ApiModelProperty(value = "截取图片的宽度")
    @Min(value = 0)
    @NotNull(message = "图片width不能为空")
    private Integer width;

    @ApiModelProperty(value = "截取图片的高度")
    @Min(value = 0)
    @NotNull(message = "图片high不能为空")
    private Integer high;
*/
    public String getIsNeedWatermark() {
        return isNeedWatermark;
    }

    public void setIsNeedWatermark(String isNeedWatermark) {
        this.isNeedWatermark = isNeedWatermark;
    }

    public String getWatermarkPosition() {
        return watermarkPosition;
    }

    public void setWatermarkPosition(String watermarkPosition) {
        this.watermarkPosition = watermarkPosition;
    }

    /*public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }*/

}
