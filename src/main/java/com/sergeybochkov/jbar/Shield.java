package com.sergeybochkov.jbar;

/** Класс содержит информацию об однотипных генерируемых наклейках */
public class Shield {
    /**
     * Конструктор класса
     * @param name ФИО поверителя
     * @param month месяц следующей поверки
     * @param year год следующей поверки
     * @param code код подразделения
     * @param num количество наклеек
     */
    public Shield(String name, int month, int year, int code, int num){
        this.name = name;
        this.month = month;
        this.year = year;
        this.code = code;
        this.num = num;
    }

    /**
     * Возвращает код подразделения
     * @return String
     */
    public String getDep(){ return String.valueOf(code); }

    /**
     * Возвращает количество однотипных наклеек
     * @return int
     */
    public int getNum() { return num; }

    /**
     * Возвращает дату следующей поверки в формате "месяц год"
     * @return String
     */
    public String getDate() {
        return Application.MONTHS_TO[month] + " " + year;
    }

    /**
     * Возвращает дату следующей поверки в формате "mm.yyyy"
     * @return String
     */
    public String getDateNum(){
        String m = (month + 1) < 10 ? "0" + (month+1) : "" + (month+1);
        return m + "." + year;
    }

    /**
     * Возвращает строку, которая кодируется в штрихкоде
     * @return String
     */
    public String getCode(){
        String m = (month + 1) < 10 ? "0" + (month+1) : "" + (month+1);
        return String.valueOf(code) + m + String.valueOf(year);
    }

    /**
     * Возвращает ФИО поверителя
     * @return String
     */
    public String getName() { return name; }

    private String name;
    private int month;
    private int year;
    private int code;
    private int num;
}
