package csu.yulin.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员 VO
 *
 * @author lp
 * @create 2025-01-02
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

   
}
