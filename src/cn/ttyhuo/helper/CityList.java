package cn.ttyhuo.helper;

import cn.ttyhuo.common.AddressData;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class CityList {
	private static List<String> provinceData;
	private static LinkedHashMap<String, String[]> cityMap;

	public static List<String> getProvinceData() {
		if (provinceData == null) {
//			init();
//			provinceData = Arrays.asList(cityMap.keySet()
//					.toArray(new String[0]));
            provinceData = Arrays.asList(AddressData.PROVINCES);
        }

		return provinceData;
	}

	public static List<String> getCity(String province) {
        //return Arrays.asList(cityMap.get(province));

        int tmpIndex = -1;
        int provinceIndex = -1;
        for(String provinceI : AddressData.PROVINCES)
        {
            tmpIndex ++;
            if(provinceI.equals(province))
                provinceIndex = tmpIndex;
        }
        if(provinceIndex > -1)
            return Arrays.asList(AddressData.CITIES[provinceIndex]);
        return Arrays.asList(AddressData.NO_LIMITS);
	}

    public static List<String> getCounty(String province, String city) {

        List<String> cityArr = Arrays.asList(AddressData.NO_LIMITS);
        int tmpIndex = -1;
        int provinceIndex = -1;
        for(String provinceI : AddressData.PROVINCES)
        {
            tmpIndex ++;
            if(provinceI.equals(province))
                provinceIndex = tmpIndex;
        }
        if(provinceIndex > -1)
            cityArr = Arrays.asList(AddressData.CITIES[provinceIndex]);

        tmpIndex = -1;
        int cityIndex = -1;
        for(String cityI : cityArr)
        {
            tmpIndex ++;
            if(cityI.equals(city))
                cityIndex = tmpIndex;
        }
        if(cityIndex > -1)
            return Arrays.asList(AddressData.COUNTIES[provinceIndex][cityIndex]);
        return Arrays.asList(AddressData.NO_LIMITS);
    }

	private static void init() {
		cityMap = new LinkedHashMap<String, String[]>();
		cityMap.put("北京市", new String[] { "北京市" });
		cityMap.put("上海市", new String[] { "上海市" });
		cityMap.put("天津市", new String[] { "天津市" });
		cityMap.put("重庆市", new String[] { "重庆市" });
		cityMap.put("河北省", new String[] { "石家庄", "唐山", "秦皇岛", "邯郸", "邢台", "保定",
				"张家口", "承德", "沧州", "廊坊", "衡水" });
		cityMap.put("山西省", new String[] { "太原", "大同", "阳泉", "长治", "晋城", "朔州",
				"晋中", "运城", "忻州", "临汾", "吕梁" });
		cityMap.put("内蒙古自治区", new String[] { "呼和浩特", "包头", "乌海", "赤峰", "通辽",
				"鄂尔多斯", "呼伦贝尔", "巴彦淖尔", "乌兰察布", "兴安", "锡林郭勒", "阿拉善" });
		cityMap.put("辽宁省", new String[] { "沈阳", "大连", "鞍山", "抚顺", "本溪", "丹东",
				"锦州", "营口", "阜新", "辽阳", "盘锦", "铁岭", "朝阳", "葫芦岛" });
		cityMap.put("吉林省", new String[] { "长春", "吉林", "四平", "辽源", "通化", "白山",
				"松原", "白城", "延边" });
		cityMap.put("黑龙江", new String[] { "哈尔滨", "齐齐哈尔", "鸡西", "鹤岗", "双鸭山",
				"大庆", "伊春", "佳木斯", "七台河", "牡丹江", "黑河", "绥化", "大兴安岭" });
		cityMap.put("江苏省", new String[] { "南京", "无锡", "徐州", "常州", "苏州", "南通",
				"连云港", "淮安", "盐城", "扬州", "镇江", "泰州", "宿迁" });
		cityMap.put("浙江省", new String[] { "杭州", "宁波", "温州", "嘉兴", "湖州", "绍兴",
				"金华", "衢州", "舟山", "台州", "丽水" });
		cityMap.put("安徽省", new String[] { "合肥", "芜湖", "蚌埠", "淮南", "马鞍山", "淮北",
				"铜陵", "安庆", "黄山", "滁州", "阜阳", "宿州", "巢湖", "六安", "亳州", "池州",
				"宣城" });
		cityMap.put("福建省", new String[] { "福州", "厦门", "莆田", "三明", "泉州", "漳州",
				"南平", "龙岩", "宁德" });
		cityMap.put("江西省", new String[] { "南昌", "景德镇", "萍乡", "九江", "新余", "鹰潭",
				"赣州", "吉安", "宜春", "抚州", "上饶" });
		cityMap.put("山东省", new String[] { "济南", "青岛", "淄博", "枣庄", "东营", "烟台",
				"潍坊", "威海", "济宁", "泰安", "日照", "莱芜", "临沂", "德州", "聊城", "滨州",
				"菏泽" });
		cityMap.put("河南省", new String[] { "郑州", "开封", "洛阳", "平顶山", "焦作", "鹤壁",
				"新乡", "安阳", "濮阳", "许昌", "漯河", "三门峡", "南阳", "商丘", "信阳", "周口",
				"驻马店" });
		cityMap.put("湖北省", new String[] { "武汉", "黄石", "襄樊", "十堰", "荆州", "宜昌",
				"荆门", "鄂州", "孝感", "咸宁", "随州", "恩施" });
		cityMap.put("湖南省", new String[] { "长沙", "株洲", "湘潭", "衡阳", "邵阳", "岳阳",
				"常德", "张家界", "益阳", "郴州", "永州", "怀化", "娄底", "湘西" });
		cityMap.put("广东省", new String[] { "广州", "深圳", "珠海", "汕头", "韶关", "佛山",
				"江门", "湛江", "茂名", "肇庆", "惠州", "梅州", "汕尾", "河源", "阳江", "清远",
				"东莞", "中山", "潮州", "揭阳", "云浮" });
		cityMap.put("广西自治区", new String[] { "南宁", "柳州", "桂林", "梧州", "北海",
				"防城港", "钦州", "贵港", "玉林", "百色", "贺州", "河池", "来宾", "崇左" });
		cityMap.put("海南省", new String[] { "海口", "三亚" });
		cityMap.put("四川省", new String[] { "成都", "自贡", "攀枝花", "泸州", "德阳", "绵阳",
				"广元", "遂宁", "内江", "乐山", "南充", "宜宾", "广安", "达州", "眉山", "雅安",
				"巴中", "资阳", "阿坝", "甘孜", "凉山" });
		cityMap.put("贵州省", new String[] { "贵阳", "六盘水", "遵义", "安顺", "铜仁", "毕节",
				"黔西南", "黔东南", "黔南" });
		cityMap.put("云南省", new String[] { "昆明", "曲靖", "玉溪", "保山", "昭通", "丽江",
				"普洱", "临沧", "文山", "红河", "西双版纳", "楚雄", "大理", "德宏", "怒江", "迪庆" });
		cityMap.put("西藏自治区", new String[] { "拉萨", "昌都", "山南", "日喀则", "那曲",
				"阿里", "林芝" });
		cityMap.put("陕西省", new String[] { "西安", "铜川", "宝鸡", "咸阳", "渭南", "延安",
				"汉中", "榆林", "安康", "商洛" });
		cityMap.put("甘肃省", new String[] { "兰州", "嘉峪关", "金昌", "白银", "天水", "武威",
				"张掖", "平凉", "酒泉", "庆阳", "定西", "陇南", "临夏", "甘南" });
		cityMap.put("青海省", new String[] { "西宁", "海东", "海北", "黄南", "海南", "果洛",
				"玉树", "海西" });
		cityMap.put("宁夏自治区", new String[] { "银川", "石嘴山", "吴忠", "固原", "中卫" });
		cityMap.put("新疆自治区", new String[] { "乌鲁木齐", "克拉玛依", "吐鲁番", "哈密", "和田",
				"阿克苏", "喀什", "克孜勒苏柯尔克孜", "巴音郭楞蒙古", "昌吉", "博尔塔拉蒙古", "伊犁哈萨克",
				"塔城", "阿勒泰" });

		cityMap.put("香港特别行政区", new String[] { "香港特别行政区" });
		cityMap.put("澳门特别行政区", new String[] { "澳门特别行政区" });
		cityMap.put("台湾省", new String[] { "台北", "高雄", "基隆", "台中", "台南", "新竹",
				"嘉义" });
	}
}
