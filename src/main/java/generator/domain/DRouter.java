package generator.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
* 
* @TableName d_router
*/
public class DRouter implements Serializable {

    /**
    * 路由ID
    */
    (message="[路由ID]不能为空")
    @ApiModelProperty("路由ID")
    private Integer id;
    /**
    * 类型
    */
    @NotBlank(message="[类型]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("类型")
    @Length(max= 255,message="编码长度不能超过255")
    private String type;
    /**
    * 路径
    */
    @NotBlank(message="[路径]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("路径")
    @Length(max= 255,message="编码长度不能超过255")
    private String path;
    /**
    * 名称
    */
    @NotBlank(message="[名称]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("名称")
    @Length(max= 255,message="编码长度不能超过255")
    private String label;
    /**
    * 图标
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("图标")
    @Length(max= 255,message="编码长度不能超过255")
    private String icon;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer parentId;

    /**
    * 路由ID
    */
    private void setId(Integer id){
    this.id = id;
    }

    /**
    * 类型
    */
    private void setType(String type){
    this.type = type;
    }

    /**
    * 路径
    */
    private void setPath(String path){
    this.path = path;
    }

    /**
    * 名称
    */
    private void setLabel(String label){
    this.label = label;
    }

    /**
    * 图标
    */
    private void setIcon(String icon){
    this.icon = icon;
    }

    /**
    * 
    */
    private void setParentId(Integer parentId){
    this.parentId = parentId;
    }


    /**
    * 路由ID
    */
    private Integer getId(){
    return this.id;
    }

    /**
    * 类型
    */
    private String getType(){
    return this.type;
    }

    /**
    * 路径
    */
    private String getPath(){
    return this.path;
    }

    /**
    * 名称
    */
    private String getLabel(){
    return this.label;
    }

    /**
    * 图标
    */
    private String getIcon(){
    return this.icon;
    }

    /**
    * 
    */
    private Integer getParentId(){
    return this.parentId;
    }

}
