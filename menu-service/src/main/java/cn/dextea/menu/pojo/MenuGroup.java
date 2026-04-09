package cn.dextea.menu.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroup {
    private String id;
    private String name;
    private Integer sort;
    private List<MenuProduct> content;

    public boolean hasProduct(Long id){
        for (MenuProduct menuProduct : content) {
            if (menuProduct.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    public void sortContent(){
        content.sort((o1, o2) -> o1.getSort() - o2.getSort());
    }

    public void deleteProduct(Long id){
        content.removeIf(menuProduct -> menuProduct.getId().equals(id));
    }

    public MenuProduct getProduct(Long id){
        for (MenuProduct menuProduct : content) {
            if (menuProduct.getId().equals(id)){
                return menuProduct;
            }
        }
        return null;
    }
}
