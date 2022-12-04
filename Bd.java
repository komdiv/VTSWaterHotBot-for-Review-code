package com.sp.bot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Bd {
    static boolean block = false; //Блокировка. Для загрузки данных.
    //private Class<SettingBd.FLI_NameColl> FilterEnumNameColl = SettingBd.FLI_NameColl.class ;
    protected Class<EnumNameColl> FilterEnumNameColl;

    public enum BdStream {Input, Output};
    private String path; //путь к файлу для input или output// Просто надо создать разные обекты и все
    private final String pathShablon;

//    private String pathOutPut; //путь к файлу для output
//    private final String pathShablonOutPut; //заполняется в зависимости от String path

    //private Class setting;
    //private SettingBd settingBd;
    private int nomDog;
    private int nameOfContragent;
    //private String TipSnab; //При создании БД обязательно указывать columnTS = Тип снабжения. 'Это один из 2 путей решения поблемы смешенных данных в файле
    private String chatID; //При сохранении данных запишу chatID кто передал
    private String nameUSer; //При сохранении данных запишу nameUSer кто передал
    private FileInputStream fileInputStream;

    //private String sheetName;
    //private Iterator iterator;

    //Это на время отладки - позднее удалить
    public Bd(String path) {
        //FLI или FLO разделяются просто созданием разных объектов
        this.path = path;//Это меняется по мере надобности на текущий файл
        this.pathShablon = path;//Это для памяти не будет меняться
        //this.pathOutPut = path.replace("FLI","FLO");//Это меняется по мере надобности на текущий файл //Поменяю FLI на FLO // Это кастыль, чтоб не добавлять на пустом месте новый параметр в конструктор
        //this.pathShablonOutPut = path.replace("FLI","FLO");//Это для памяти не будет меняться//Поменяю FLI на FLO // Это кастыль, чтоб не добавлять на пустом месте новый параметр в конструктор
    }

    //Это перспективное решениею Может и приживется
//    public Bd(String path, Class setting) {
//        this.path = path;
//        System.out.println("Класс параметров = " + setting.getClass());
//        this.setting = setting;
//    }

//    private void getEnum(Class aEnum){
//        setting = aEnum;
//    }

    public XSSFSheet activeSheet(String name) throws IOException {
        File file = new File(this.path);
        //System.out.println("new File(this.path) = " + this.path);
        //FileInputStream fileInputStream = new FileInputStream(new File(this.path));
        FileInputStream fileInputStream = new FileInputStream(file);
        //this.fileInputStream = fileInputStream;
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = workbook.getSheet(name);
        //fileInputStream.close();
        return sheet;
    }

    public XSSFSheet activeSheetStreamIO(String name, BdStream bdStream) throws IOException {
        File file = new File(this.path);
        XSSFWorkbook workbook;
        switch (bdStream) {
            case Input:
                //FileInputStream fileInputStream = new FileInputStream(new File(this.path));
                FileInputStream fileInputStream = new FileInputStream(file);
                workbook = new XSSFWorkbook(fileInputStream);
                XSSFSheet sheet = workbook.getSheet(name);
                return sheet;

            case Output:
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                //workbook = new XSSFWorkbook(fileOutputStream);
                //XSSFSheet sheet = workbook.getSheet(name);
                //return sheet;
                break;
        }
        return null;
    }




    public String find(String what, String nameSheet, int p1, int p2) throws IOException {
        int column=p1-1;
        String otvet="ничего не найдено !";

        for(Row row: activeSheet(nameSheet)){
            //System.out.println("Начинается поиск");
            if(row.getCell(column)!=null && row.getCell(column).getCellType()== CellType.STRING){
                if (row.getCell(column).getStringCellValue().equals(what)){
                    otvet = row.getCell(p2-1).getStringCellValue();
                    System.out.println("Нашли "+ what +" и узнали что это => " + otvet);
                    return otvet;
                }
            if(row.getCell(column)==null) return "ничего не найдено 1";
            }
        }

        return "Искали " + what +". ИТОГ = " + otvet;
    }

    public String find10(String what, String nameSheet, int p1, int p2) throws IOException {
        p1=p1-1;
        p2=p2-1;

        String otvet="";
        int nomStrInt = 0;
        int count = 0;
        for(Row row: activeSheet(nameSheet)){
            nomStrInt++;
            //System.out.println("Начинается поиск");
            if(nomStrInt>1) {   //Пропуск шапочки на 1 строке
                if (row.getCell(p1) != null && row.getCell(p1).getCellType() == CellType.STRING) {
                    //if (row.getCell(p1).getStringCellValue().equals(what)){
                    if (row.getCell(p1).getStringCellValue().toLowerCase().indexOf(what.toLowerCase()) >= 0) {
                        count++;
                        System.out.println("Нашли " + what + " в \"" + row.getCell(p1).getStringCellValue() + "\n");
                        //otvet =  otvet + "< " + count + " > "+ row.getCell(p2).getStringCellValue() + "\n"; //ошибка парсинга html
                        otvet =  otvet + "[ " + count + " ] "+ row.getCell(p2).getStringCellValue() + "\n";
                        if (count==10) {
                            //System.out.println("Нашли " + what + " в \"" + row.getCell(p1).getStringCellValue() + "\" и узнали что это => " + otvet);
                            return otvet;
                        }
                    }
                    if (row.getCell(p1) == null) return otvet;
                }
            }
        }
        if(otvet=="") otvet="ничего не найдено !";
        //return "Искали " + what +". ИТОГ = " + otvet;
        return otvet;
    }

    public HashMap<EnumNameColl,String> findParaMap(String what, String nameSheet, int p1, HashMap<EnumNameColl,String> p2) throws IOException {
        //HashMap<EnumNameColl, String> car;
        int column=p1-1;
        String otvet="ничего не найдено!";
        for(Row row: activeSheet(nameSheet)){
            //System.out.println("Начинается поиск");
            if(row.getCell(column)!=null && row.getCell(column).getCellType()== CellType.STRING){
                //System.out.println("Начинается поиск. Текущий лс = " + row.getCell(column).getStringCellValue());
                //System.out.println("what = " + what);
                if (row.getCell(column)!=null && row.getCell(column).getStringCellValue().equals(what)){
                    //Нашли нужную строку с нужным What
                    Iterator<Map.Entry<EnumNameColl,String>> iterator = p2.entrySet().iterator();
                    while (iterator.hasNext()){
                        //otvet="";
                        Map.Entry<EnumNameColl,String> tekStr =  iterator.next();
                        //int kolonka = tekStr.getKey().getNumberColl()-1;
                        //System.out.println("tekStr.getKey() = " + tekStr.getKey() + " ==>> " + kolonka);

                        if(row.getCell(tekStr.getKey().getNumberColl() - 1)!=null) {
                            otvet = row.getCell(tekStr.getKey().getNumberColl() - 1).getStringCellValue();// -1!!! Потому как EnumNameColl нумеруется с 1(а не 0) как в Excel
                        }else {
                            otvet = "";
                        }
                        tekStr.setValue(otvet);
                        //System.out.println("По "+ what +" нашли [" + tekStr.getKey() + "] и узнали что это => " + otvet);
                    }
//                    otvet = row.getCell()
//                    otvet = row.getCell(p2-1).getStringCellValue();
                    return p2;
                }
                //if(row.getCell(column)==null) return p2;
            }
        }
        //Раз мы тут, значит What не нашли
        //Если ничего не нашли, то ничем данные и не заменили
        //Вопрос что делать со старыми данными. Ведь сюда зашли чтобы заполнить новыми данными, а новые данные получается пустые.
        //Тогда все заполню пустыми данными. Вроде логично немного.
        //return "Искали " + what +". ИТОГ = " + otvet;
        Iterator<Map.Entry<EnumNameColl,String>> iterator = p2.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<EnumNameColl,String> strTek = iterator.next();
            strTek.setValue(null);
        }
        return p2;
    }

    public LinkedHashMap<EnumNameColl,String> getParaMap(Row row, HashSet<EnumNameColl> collALL, LinkedHashMap<EnumNameColl,String> filterCollNeed) throws IOException {
        //по найденной ROW
        //Из общего списка столбцов collAll - это БД где все столбцы
        //Заполняет только нужные столбцы значениями в filterCollNeed
        String otvet="ничего не найдено!";
        //Переберу все имеющиеся столбцы
        Iterator<EnumNameColl> iterator = collALL.iterator();
        String value="";//Тут будет ответ из Cell строки Row
        while (iterator.hasNext()){
            EnumNameColl collTek = iterator.next();
            //узнаю нужен ли тек столбец в ответе
            //System.out.println("из ALL взяли " + collTek);
            Set<EnumNameColl> needSet = filterCollNeed.keySet();
            Iterator<EnumNameColl> iteratorNeed = needSet.iterator();
            while (iteratorNeed.hasNext()){
                EnumNameColl collTek_Need = iteratorNeed.next();
                if(collTek_Need.equals(collTek)){
                    //Тут - значит колонка collTek нужна
                    //проверю на null
                    if(row.getCell(collTek.getNumberColl()-1)!=null) {
                        value = row.getCell(collTek.getNumberColl() - 1).getStringCellValue();
                        //filterCollNeed.put(collTek, value);
                    }else { //Тут - значит ячейка пуста (ее даже нет)
                        value="";
                        filterCollNeed.put(collTek_Need, value);
                    }
                    //Нужный столбец найден и обработан. Можно прерваться и переходить к следующему
                    //System.out.println("нашли нужное collTek_Need =" + collTek_Need + " = > " + value);
                    filterCollNeed.put(collTek_Need, value);

                    break;
                }
            }
        }
        return filterCollNeed;
    }



    public void bd_LoadCSV(XSSFSheet sheet, String pathBdFromCSV) throws IOException {
        boolean allPart = true;
        int i=40000;
        int step = 5000;
        //allPart = HashBd_LoadCSV_Part(i,i+step,1, sheet.getSheetName(), pathBdFromCSV);
        //allPart = bd_LoadCSV_Part(i,i+step, sheet.getSheetName(), pathBdFromCSV);
//        while (allPart) {
//            allPart = bd_LoadCSV_Part(i,i+step, sheet.getSheetName(), pathBdFromCSV);
//            i=i+step;
//            //bd_LoadCSV_Part(10, 15, sheet.getSheetName(), pathBdFromCSV);
//        }
    }

    private boolean bd_LoadCSV_Part(int fromStr, int toStr, String sheetName, String pathBdFromCSV) throws IOException {
        //!!! Позже переделать параметр sheet на просто String
        //!!! Возможно надо закрывать inputStream
        //Создам пустую книгу в памяти
        File fileExcel=null;
        XSSFWorkbook workbook=null;
        XSSFSheet activeSheet=null;

//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet newSheet = workbook.createSheet(sheet.getSheetName());
        Row row;
        Cell cell;
        int rowNum=0;
        //
        File file_FLI_csv = new File(pathBdFromCSV);
        FileReader fileReader = new FileReader(file_FLI_csv);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String rowCsv = "";
        while(( rowCsv = bufferedReader.readLine()) != null){
            //Добавляем строки если они в диапазоне
            if(rowNum>=fromStr) {
                if(rowNum==fromStr){
                    if(rowNum==0){ //При первой строке создам новый файл
                        workbook = new XSSFWorkbook();
                        activeSheet = workbook.createSheet(sheetName);
                        fileExcel = new File(this.getPath());
                        FileOutputStream fileOutputStream = new FileOutputStream(fileExcel);
                        workbook.write(fileOutputStream);
                        fileOutputStream.close();
                        System.out.println("Создан файл с листом =  " + sheetName);
                    }
                    //Файл создан уже выше. Открою его.
                    fileExcel = new File(this.getPath());
                    if (getFileInputStream()!=null) getFileInputStream().close();
                    FileInputStream fileInputStream = new FileInputStream(fileExcel);
                    workbook = new XSSFWorkbook(fileInputStream);
                    activeSheet = workbook.getSheet(sheetName);
                }
                if(activeSheet!=null) {//Имеется activeSheet значит работаем:
                    String[] data = rowCsv.split(";");
                    int i = 0;
                    row = activeSheet.createRow(rowNum);
                    for (String di : data) {
                        cell = row.createCell(i++, CellType.STRING);
                        if (rowNum == 0) cell.setCellValue(di);//Шапочка
                        if (rowNum > 0) cell.setCellValue(di.substring(1, di.length() - 1));//Данные
//                if (rowNum==10000 && rowNum%5000==0){
//                di.substring(1,di.length()-1);
//                cell.setCellValue(di);
                        //System.out.print("<"+ i +"> " + di);
                    }
                    if (rowNum == toStr) {//Конец Работы. Сохраним файл
                        System.out.println("Загрузка кончилась - начало сохранения данных");
                        //if (fileInputStream!=null) fileInputStream.close();
                        if (getFileInputStream()!=null) getFileInputStream().close();
                        //File file = new File(this.getPath());
                        FileOutputStream fileOutputStream = new FileOutputStream(fileExcel);
                        workbook.write(fileOutputStream);
                        fileOutputStream.close();
                        System.out.println("СОХРАНЕНО при rowNum =  " + rowNum);
                        return true;
                        //ПОВТОР
                        //workbook = new XSSFWorkbook();
                        //newSheet = workbook.getSheet(sheet.getSheetName());
                        //file_FLI_csv = new File(pathBdFromCSV);
                        //fileReader = new FileReader(file_FLI_csv);
                        //bufferedReader = new BufferedReader(fileReader);
                    }
                }
            }
            System.out.println(" rowNum =  " + rowNum);
            rowNum++;
        }
        File file = new File(this.getPath());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        return false;
    }



    //------------------Get------------------------------------------------

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getNameUSer() {
        return nameUSer;
    }

    public void setNameUSer(String nameUSer) {
        this.nameUSer = nameUSer;
    }

    public String getPathShablon() {
        return pathShablon;
    }

    public FileInputStream getFileInputStream() {
        return fileInputStream;
    }

    public String getPath() {
        return path;
    }

    public int getNomDog() {
        return nomDog;
    }

    public int getNameOfContragent() {
        return nameOfContragent;
    }

    public Class<EnumNameColl> getFilterEnumNameColl() {
        return FilterEnumNameColl;
    }


    //--------------------Set----------------------------------------------
    public void setFileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setNomDog(int nomDog) {
        this.nomDog = nomDog;
    }

    public void setNameOfContragent(int nameOfContragent) {
        this.nameOfContragent = nameOfContragent;
    }

//    public void setFilterEnumNameColl(EnumNameColl filterEnumNameColl) {
//        FilterEnumNameColl = filterEnumNameColl;
//    }
    public void setFilterEnumNameColl(Class<EnumNameColl> filterEnumNameColl) {
        FilterEnumNameColl = filterEnumNameColl;
    }

}



