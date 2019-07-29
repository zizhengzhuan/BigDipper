package com.z3pipe.z3core.config;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-13
 * Time: 17:20
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
public enum Week {
    //周
    MONDAY("星期一", "Monday", "Mon.", 1),
    TUESDAY("星期二", "Tuesday", "Tues.", 2),
    WEDNESDAY("星期三", "Wednesday", "Wed.", 3),
    THURSDAY("星期四", "Thursday", "Thur.", 4),
    FRIDAY("星期五", "Friday", "Fri.", 5),
    SATURDAY("星期六", "Saturday", "Sat.", 6),
    SUNDAY("星期日", "Sunday", "Sun.", 7);

    String nameCn;
    String nameEn;
    String nameEnShort;
    int number;

    private Week(String nameCn, String nameEn, String nameEnShort, int number) {
        this.nameCn = nameCn;
        this.nameEn = nameEn;
        this.nameEnShort = nameEnShort;
        this.number = number;
    }

    public String getChineseName() {
        return this.nameCn;
    }

    public String getName() {
        return this.nameEn;
    }

    public String getShortName() {
        return this.nameEnShort;
    }

    public int getNumber() {
        return this.number;
    }
}
