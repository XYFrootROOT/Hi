package com.example.model

import androidx.compose.ui.graphics.Color

object SpeedDialData {
    val defaultSpeedDials = listOf(
        SpeedDialItem("百度", "https://www.baidu.com", "百", Color(0xFF2932E1)),
        SpeedDialItem("哔哩哔哩", "https://m.bilibili.com", "B", Color(0xFFFB7299)),
        SpeedDialItem("知乎", "https://www.zhihu.com", "知", Color(0xFF0066FF)),
        SpeedDialItem("GitHub", "https://github.com", "G", Color(0xFF24292E)),
        SpeedDialItem("维基百科", "https://zh.m.wikipedia.org", "维", Color(0xFF636466)),
        SpeedDialItem("淘宝", "https://main.m.taobao.com", "淘", Color(0xFFFF5000)),
        SpeedDialItem("新浪微博", "https://m.weibo.cn", "微", Color(0xFFE6162D)),
        SpeedDialItem("豆瓣", "https://m.douban.com", "豆", Color(0xFF00B51D)),
        SpeedDialItem("V2EX", "https://www.v2ex.com", "V", Color(0xFF333333)),
        SpeedDialItem("央视网", "https://m.cctv.com", "央", Color(0xFFC4030B)),
        SpeedDialItem("IT之家", "https://m.ithome.com", "IT", Color(0xFFD22222)),
        SpeedDialItem("天气预测", "https://wttr.in", "天", Color(0xFF0284C7))
    )

    val defaultNewsList = listOf(
        NewsItem(
            id = "1",
            title = "国家航天局发布最新深空探测与月球科研站规划",
            source = "新华网",
            timeAgo = "10分钟前",
            category = "科技",
            url = "https://www.xinhuanet.com",
            readCount = "28万阅读"
        ),
        NewsItem(
            id = "2",
            title = "人工智能大模型应用加速落地，重塑企业数字化转型",
            source = "人民网",
            timeAgo = "25分钟前",
            category = "数码",
            url = "https://www.people.com.cn",
            readCount = "15万阅读"
        ),
        NewsItem(
            id = "3",
            title = "全球科技创新论坛开启：探索未来可再生能源与智能交通",
            source = "科技日报",
            timeAgo = "1小时前",
            category = "前沿",
            url = "https://www.stdaily.com",
            readCount = "9.5万阅读"
        ),
        NewsItem(
            id = "4",
            title = "开源社区火爆发展：数百个新兴开源项目推动技术变革",
            source = "开源中国",
            timeAgo = "2小时前",
            category = "编程",
            url = "https://www.oschina.net",
            readCount = "12万阅读"
        ),
        NewsItem(
            id = "5",
            title = "2026中国移动互联网趋势报告：极速浏览与隐私安全成核心诉求",
            source = "36氪",
            timeAgo = "3小时前",
            category = "资讯",
            url = "https://36kr.com",
            readCount = "45万阅读"
        )
    )
}
