package bean

/**
 * @author longlong
 * @create 2020 07 06 14:21
 * @Describe: 该类为order样例类，用作json解析
 */
case class OrderBean(id: String,
                     province_id: String,
                     var consignee: String,  // 用户
                     order_comment: String,
                     var consignee_tel: String,  // 用户电话
                     order_status: String,
                     payment_way: String,
                     user_id: String,
                     img_url: String,
                     total_amount: Double,
                     expire_time: String,
                     delivery_address: String,
                     create_time: String,
                     operate_time: String,
                     tracking_no: String,
                     parent_order_id: String,
                     out_trade_no: String,
                     trade_body: String,
                     var create_date: String = null,
                     var create_hour: String = null
                    ){
  // 给创建日期和创建小时赋值
  create_date = create_time.substring(0, 10)
  create_hour = create_time.substring(11, 13)

  // 给用户和电话做脱敏处理
  consignee = consignee.substring(0, 1) + "**"
  consignee_tel = consignee_tel.substring(0, 3) + "****" + consignee_tel.substring(7, 11)
}
