package com.yxh.reggie.dto;
import com.yxh.reggie.entity.OrderDetail;
import com.yxh.reggie.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
