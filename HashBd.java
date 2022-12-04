package com.sp.bot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class HashBd extends Bd{
    private String[] indexHBd = {"0","1","2","3","4","5","6","7","8","9"};
    private String pathHashBd_TekIndex; //База FLI и FLO созданы как раззные объекты. Они не пересекаются
    private String pathHashBdOUT_TekIndex;
    private SettingBd.TipSnab tipSnab;  //для разделения товаров по номенклатуре
    private SettingBd.nameFirm nameFirm;// для связи Bd с конкретной организацией (для создания фильтров)

    public HashBd(String path){
        super(path);
    }

    public HashBd(String path, SettingBd.TipSnab tipSnab){
        super(path);
        this.tipSnab = tipSnab;
    }

    private void createHBd() {
        for (String index: this.indexHBd) {

        }
    }

    public static void createNewFile_SCV(String pathToFileCSV, EnumNameColl[] nameColl_Head){
        //////////////////////////////////////////////////////////////////////////////////////////////////
        //-----------------------Создам CSV с шапочкой--------------------------
        File file_FLI_csv = new File(pathToFileCSV);
        FileWriter fileWriterNew = null;
        try {
            fileWriterNew = new FileWriter(file_FLI_csv,false);//новый файл
        } catch (IOException e) {
            System.out.println("Ошибка от createFile_SCV - нет доступа к открытию файла                 !");
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
            for (EnumNameColl enumNameColl: nameColl_Head) {
                stringBuilder.append(enumNameColl.toString());
                stringBuilder.append(";");
            }
            String otvet = stringBuilder.toString();
            otvet = otvet.substring(0,otvet.length()-1);
        try {
            fileWriterNew.write(otvet + "\n");
            fileWriterNew.close();
        } catch (IOException e) {
            System.out.println("Ошибка от createFile_SCV - нет доступа к закрытию файла                 !");
            e.printStackTrace();
        }
        //=======================Создам CSV с шапочкой, если файла не существует==========================
        //////////////////////////////////////////////////////////////////////////////////////////////////
    }

    //bdTek.bd_LoadCSV(bdTek.activeSheet("FLI"), bot.getPathBd_FLI_csv());
    public void hashBd_saveCSV(String sheetName, String pathBd_FLO_csv) throws IOException, MyError {
        //очище файл pathBd_FLO_csv (это CSV - файл ответа итоговый по всем indexHash)
        //Отменю очистку - буду дописывать далее данные по фирме из разных файлов
        //FileWriter fos = new FileWriter(pathBd_FLO_csv,false);
        //fos.write("");//очищу файл перед заливкой// Не тут надо очищать
        //fos.close();
        for (int kodHashi=0;kodHashi<=9;kodHashi++) hashBd_saveCSV_Part(0,70000,kodHashi,sheetName,  pathBd_FLO_csv);
        //for (int kodHashi=0;kodHashi<=9;kodHashi++) hashBd_SaveCSV_Part(0,70000,kodHashi,sheetName,  pathBd_FLO_csv);
    }

    private void hashBd_saveCSV_Part(int fromStr, int toStr, int indexHash, String sheetName, String pathCSVFromHashBd) throws IOException {
        //Выгружаем из Bd данные в файл CSV
        //2022 08 31 нужен фильтр по организации (выгрузка идет по строкам где нужная организация)
        //System.out.println("go go");
        setPathHashBd_TekIndex(indexHash);
        System.out.println("Текущий файл БД = " + this.getPath() + " CSV = " + pathCSVFromHashBd);
        File fileSCV = new File(pathCSVFromHashBd);
        FileWriter fos = new FileWriter(fileSCV,true);
        //откроем БД
        setPathHashBd_TekIndex(indexHash);
        XSSFWorkbook workbook = new XSSFWorkbook(getPathHashBd_TekIndex());
        XSSFSheet sheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = sheet.rowIterator();/////Главная строка
        //пишу через SettingBd.FLO_NameColl_CSV тут из соображений что у всех выходные файлы CSV одинаковые. Когда станут разными - то перепишу универсально
        HashSet<EnumNameColl> filterFLO = new HashSet<EnumNameColl>();//Это БД с данными
        filterFLO.addAll(Arrays.asList(SettingBd.FLO_NameColl.values()));
        //System.out.println("Set filterFLO = " + filterFLO);

        //Порядок должен соблюдаться при добавлении. Поэтому заюзаю ArrayList
        ArrayList<EnumNameColl> filterFLO_CSV = new ArrayList<EnumNameColl>(); //Это фильтр нужных столбцов для CSV
        filterFLO_CSV.addAll(Arrays.asList(SettingBd.FLO_NameColl_CSV.values()));
        Iterator<EnumNameColl> iteratorFilterFlo = filterFLO_CSV.iterator();

        LinkedHashMap<EnumNameColl,String> filterFLOLinkedMap = new LinkedHashMap<EnumNameColl, String>();
        while (iteratorFilterFlo.hasNext()){
            EnumNameColl enumNameColl = iteratorFilterFlo.next();
            filterFLOLinkedMap.put(enumNameColl,"");
        }
        //HashMap<EnumNameColl,String> filterFLO_CSV_hashMap = (HashMap<EnumNameColl, String>)filterFLOLinkedMap;

        //setFilterEnumNameColl((EnumNameColl.class)SettingBd.FLO_NameColl.class);//Установка фильтра для выгрузки только нужных столбцов
        //EnumNameColl filter = getFilterEnumNameColl();
        //SettingBd.FLO_NameColl chatId = (SettingBd.FLO_NameColl)filter.getClass();
        int nppi=0;
        while (iterator.hasNext()){
            //Перебор строк БД
            Row rowTek = iterator.next();
            nppi++;//подсчет строк по порядку в БД
            if (nppi==1){
                //тут - значит первая строка
                //перепрыгну первую строку у всех  indexHash > 0 (только на indexHash==0 будет шапка)
                if (indexHash>=0) {  //>=0   Всегда перепрыгну через первую строку(Шапочку)
                    if(iterator.hasNext()) {rowTek = iterator.next();} //переход если есть куда переходить
                    else {break;} //если нет строк кроме заголовка - то выйду
                }
                if (indexHash==0) {
                    //System.out.println("вывод заголовка 1 раз");
                }
            }
            filterFLOLinkedMap = getParaMap(rowTek,filterFLO,filterFLOLinkedMap);//заполняет данными filterFLOLinkedMap для вывода одной строки в CSV

            //Тут узнаю нажна ли данная "Организация" (столбец = SettingBd.FLO_NameColl.База_данных)
            System.out.println("Требуемая фирма = " + this.getNameFirm());
            System.out.println("Текущая фирма в файле = " + filterFLOLinkedMap.get(SettingBd.FLO_NameColl_CSV.База_данных));
            if(this.getNameFirm().valueStrBdEquals(filterFLOLinkedMap.get(SettingBd.FLO_NameColl_CSV.База_данных))){
                //Тут значит правильная фирма
                System.out.println("Фирмы совпали");
            }else {
                System.out.println("НЕ СОВПАЛИ фирмы");
                continue; // уходим на следующую итерацию while
            }

            //System.out.println("filterFLO = " + filterFLO);
            //System.out.println("filterFLOLinkedMap = " + filterFLOLinkedMap);

            //Получили filterFLOLinkedMap где лежит готовая строка для записи в CSV
            Set<Map.Entry<EnumNameColl,String>> saveRow =  filterFLOLinkedMap.entrySet();
            Iterator<Map.Entry<EnumNameColl,String>> iteratorRow = saveRow.iterator();
            String otvetRow="";
            while (iteratorRow.hasNext()){
                Map.Entry<EnumNameColl,String> element = iteratorRow.next();
                otvetRow = element.getValue();

                if (otvetRow.indexOf("\"")==0) {
                    otvetRow = otvetRow.replaceAll("\"" , "");
                }
                //System.out.println("в файл -> " + element.getKey() + " = " + otvetRow);
                //у последней не будет ";"
                if(iteratorRow.hasNext()) {
                    fos.write("" + "\"" + otvetRow + "\";"); //не последняя - ведь есть еще
                }else {
                    fos.write("" + "\"" + otvetRow + "\""); // последняя
                }
            }
            fos.write("\n");
        }
        workbook.close();
        fos.close();
    }

    ////////////////////копия 2022 08 31
//    private void hashBd_saveCSV_Part(int fromStr, int toStr, int indexHash, String sheetName, String pathCSVFromHashBd) throws IOException {
//        //System.out.println("go go");
//        setPathHashBd_TekIndex(indexHash);
//        System.out.println("Текущий файл БД = " + this.getPath() + " CSV = " + pathCSVFromHashBd);
//        File fileSCV = new File(pathCSVFromHashBd);
//        FileWriter fos = new FileWriter(fileSCV,true);
//        //откроем БД
//        setPathHashBd_TekIndex(indexHash);
//        XSSFWorkbook workbook = new XSSFWorkbook(getPathHashBd_TekIndex());
//        //        FileInputStream fileInputStream = new FileInputStream(workbook);
//        XSSFSheet sheet = workbook.getSheet(sheetName);
//        Iterator<Row> iterator = sheet.rowIterator();
//        //пишу через SettingBd.FLO_NameColl_CSV тут из соображений что у всех выходные файлы CSV одинаковые. Когда станут разными - то перепишу универсально
//        HashSet<EnumNameColl> filterFLO = new HashSet<EnumNameColl>();//Это БД с данными
//        filterFLO.addAll(Arrays.asList(SettingBd.FLO_NameColl.values()));
//        //System.out.println("Set filterFLO = " + filterFLO);
//        //Порядок должен соблюдаться при добавлении. Поэтому заюзаю ArrayList
//        ArrayList<EnumNameColl> filterFLO_CSV = new ArrayList<EnumNameColl>(); //Это фильтр нужных столбцов для CSV
//        filterFLO_CSV.addAll(Arrays.asList(SettingBd.FLO_NameColl_CSV.values()));
//
//        LinkedHashMap<EnumNameColl,String> filterFLOLinkedMap = new LinkedHashMap<EnumNameColl, String>();
//        Iterator<EnumNameColl> iteratorFilterFlo = filterFLO_CSV.iterator();
//        while (iteratorFilterFlo.hasNext()){
//            EnumNameColl enumNameColl = iteratorFilterFlo.next();
//            filterFLOLinkedMap.put(enumNameColl,"");
//        }
//        //HashMap<EnumNameColl,String> filterFLO_CSV_hashMap = (HashMap<EnumNameColl, String>)filterFLOLinkedMap;
//
//        //setFilterEnumNameColl((EnumNameColl.class)SettingBd.FLO_NameColl.class);//Установка фильтра для выгрузки только нужных столбцов
//        //EnumNameColl filter = getFilterEnumNameColl();
//        //SettingBd.FLO_NameColl chatId = (SettingBd.FLO_NameColl)filter.getClass();
//        int nppi=0;
//        while (iterator.hasNext()){
//            //Перебор строк БД
//            Row rowTek = iterator.next();
//            nppi++;//подсчет строк по порядку в БД
//            if (nppi==1){
//                //тут - значит первая строка
//                //перепрыгну первую строку у всех  indexHash > 0 (только на indexHash==0 будет шапка)
//                if (indexHash>0) {
//                    if(iterator.hasNext()) {rowTek = iterator.next();} //переход если есть куда переходить
//                    else {break;} //если нет строк кроме заголовка - то выйду
//                }
//                if (indexHash==0) {
//                    //System.out.println("вывод заголовка 1 раз");
//                }
//            }
//            filterFLOLinkedMap = getParaMap(rowTek,filterFLO,filterFLOLinkedMap);//заполняет данными filterFLOLinkedMap для вывода одной строки в CSV
//            //System.out.println("filterFLO = " + filterFLO);
//            //System.out.println("filterFLOLinkedMap = " + filterFLOLinkedMap);
//
//            //Получили filterFLOLinkedMap где лежит готовая строка для записи в CSV
//            Set<Map.Entry<EnumNameColl,String>> saveRow =  filterFLOLinkedMap.entrySet();
//            Iterator<Map.Entry<EnumNameColl,String>> iteratorRow = saveRow.iterator();
//            String otvetRow="";
//            while (iteratorRow.hasNext()){
//                Map.Entry<EnumNameColl,String> element = iteratorRow.next();
//                otvetRow = element.getValue();
//
//                if (otvetRow.indexOf("\"")==0) {
//                    otvetRow = otvetRow.replaceAll("\"" , "");
//                }
//                //System.out.println("в файл -> " + element.getKey() + " = " + otvetRow);
//                //у последней не будет ";"
//                if(iteratorRow.hasNext()) {
//                    fos.write("" + "\"" + otvetRow + "\";"); //не последняя - ведь есть еще
//                }else {
//                    fos.write("" + "\"" + otvetRow + "\""); // последняя
//                }
//            }
//            fos.write("\n");
//        }
//        workbook.close();
//        fos.close();
//    }
    //////////////////////////////////конец копии от 2022 08 31

    public void hashBd_loadCSV(String sheetName, String pathBd_FLI_csv) throws IOException, MyError {
        //indexHash будет перебираться тут
        //Если начинается с 0 - то создается новый файл с шапочкой и сразу же выход
        //System.out.println(""+getHash("4790030"));
        //----------------Рабочая загрузка---------------------
        for (int kodHashi=0;kodHashi<=9;kodHashi++) hashBd_LoadCSV_Part(0,70000,kodHashi,sheetName,  pathBd_FLI_csv);
        //================Рабочая загрузка=====================
        //this.indexHBd[1]
        //hashBd_LoadCSV_Part(0,70000, Integer.parseInt(this.getIndexHBd()[1]) ,sheetName,  pathBd_FLI_csv);

        //hashBd_LoadCSV_Part(1,5000,1,sheetName,  pathBd_FLI_csv);
    }
    private boolean hashBd_LoadCSV_Part(int fromStr, int toStr,int indexHash, String sheetName, String pathBdFromCSV) throws IOException, MyError {
        //!!! Позже переделать параметр sheet на просто String
        //!!! Возможно надо закрывать inputStream
        //Создам пустую книгу в памяти
        setPathHashBd_TekIndex(indexHash);

        File fileExcel=null;
        fileExcel = new File(getPathHashBd_TekIndex());

        XSSFWorkbook workbook=null;
        XSSFSheet activeSheet=null;
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet newSheet = workbook.createSheet(sheet.getSheetName());
        boolean needSaveRow = true;
        Row row;
        Cell cell;
        int rowNum=0;  //номер строки по CSV
        int rowNumExcel=0;  //номер строки по Excel

        //////////////////////////////////////////////////////////////////////////////////////////////////
        //-----------------------Создам CSV с шапочкой, если файла не существует--------------------------
        File file_FLI_csv = new File(pathBdFromCSV); //это используется и для FLI и для FLO
        if (!file_FLI_csv.exists()){
            //Тут пишу токо для FLO. А файлы FLI дб обязательно.
            //Возьму заголовки столбцов из SettingBd.FLO_NameColl - запишу их в csv, а от туда они автоматом попадут в xlsx
            EnumNameColl[] FLO_coll = SettingBd.FLO_NameColl.values();
            //если файл не существует - то создам его пустой
            FileWriter fileWriterNew = new FileWriter(file_FLI_csv);
            StringBuilder stringBuilder = new StringBuilder();
            for (EnumNameColl enumNameColl: FLO_coll) {
                stringBuilder.append(enumNameColl.toString());
                stringBuilder.append(";");
            }
            String otvet = stringBuilder.toString();
            otvet = otvet.substring(0,otvet.length()-1);
            fileWriterNew.write(otvet + "\n");
            fileWriterNew.close();
        }
        //=======================Создам CSV с шапочкой, если файла не существует==========================
        //////////////////////////////////////////////////////////////////////////////////////////////////


        FileReader fileReader = new FileReader(file_FLI_csv);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String rowCsv = "";
        while(( rowCsv = bufferedReader.readLine()) != null){
            needSaveRow = true;
            //Добавляем строки если они в диапазоне
            if(rowNum>=fromStr) {
                if(rowNum==fromStr){
                    if(rowNum==0){ //При первой строке создам новый файл (а вдруг файл уже создан и  надо лишь дописать в него а не создавать заново)
                        ////////////проверю, был ли ранее создан этот файл другими загрузками
                        if(fileExcel.exists()) {
                            workbook = new XSSFWorkbook( new FileInputStream(fileExcel));
                            activeSheet = workbook.getSheet(sheetName);
                            rowNumExcel=activeSheet.getLastRowNum();  //номер строки по Excel при загрузке нового csv  в существующий excel
                            rowNumExcel++;//Это будет номер первой пустой row
                        }else {
                        workbook = new XSSFWorkbook();
                        activeSheet = workbook.createSheet(sheetName);
                        //String DDDD = this.getPath().substring(0,this.getPath().lastIndexOf("."))+"_"+indexHash+this.getPath().substring(this.getPath().lastIndexOf("."));
                        String DDDD = getPathHashBd_TekIndex();
                        fileExcel = new File(getPathHashBd_TekIndex());
                        /////-------------------------------Создам файл и шапочку
                            if(activeSheet!=null) {//Имеется activeSheet значит работаем:
                            String[] dataCap = rowCsv.split(";");

                            int i = 0;
                            row=null;
                            for (String di : dataCap) {
                                //вычислю hash от лицевого счета
                                //if(i==0)System.out.println("Hash Лиц Счета = " + getHash(di));

                                //if(i==0 && getHash(di)==indexHash) row = activeSheet.createRow(rowNumExcel++);
                                if(i==0) row = activeSheet.createRow(rowNumExcel++);

                                if (row!=null) {
                                    cell = row.createCell(i++, CellType.STRING);
                                    if (rowNum == 0) cell.setCellValue(di);//Шапочка
                                    if (rowNum > 0) cell.setCellValue(di.substring(1, di.length() - 1));//Данные
//                if (rowNum==10000 && rowNum%5000==0){
//                di.substring(1,di.length()-1);
//                cell.setCellValue(di);
                                    //System.out.print("<"+ i +"> " + di);
                                }else{
                                    break;}
                            }
                            //Шапочка создана. Конец создания Файла. Сохраним файл
//                                System.out.println("Загрузка шапки кончилась - начало сохранения данных");
//                                if (getFileInputStream()!=null) getFileInputStream().close();
//                                //File file = new File(this.getPath());
//                                FileOutputStream fileOutputStreamCap = new FileOutputStream(fileExcel);
//                                workbook.write(fileOutputStreamCap);
//                                fileOutputStreamCap.close();
//
//                                System.out.println("СОХРАНЕНА ШАПКА при rowNum =  " + rowNum);
                                //return true;
                                //ПОВТОР
                                //workbook = new XSSFWorkbook();
                                //newSheet = workbook.getSheet(sheet.getSheetName());
                                //file_FLI_csv = new File(pathBdFromCSV);
                                //fileReader = new FileReader(file_FLI_csv);
                                //bufferedReader = new BufferedReader(fileReader);

                        }
                        /////===============================Создам файл и шапочку=======================================
                        //Это дублирующий код?
                        if (getFileInputStream()!=null) getFileInputStream().close();
                        FileOutputStream fileOutputStream = new FileOutputStream(fileExcel);
                        workbook.write(fileOutputStream);
                        fileOutputStream.close();
                        System.out.println("Создан файл с листом =  " + sheetName);
                        }
                    }
                    //Файл создан уже выше. Открою его.
                    fileExcel = new File(getPathHashBd_TekIndex());
                    if (getFileInputStream()!=null) getFileInputStream().close();
                    setFileInputStream(new FileInputStream(fileExcel));
                    //FileInputStream fileInputStream = new FileInputStream(fileExcel);
                    workbook = new XSSFWorkbook(getFileInputStream());
                    activeSheet = workbook.getSheet(sheetName);
                }
                if(activeSheet!=null && rowNum>0) {//Имеется activeSheet значит работаем: Пропустив заголовок
                    if ((rowNumExcel==9437)){
                        System.out.println("стоп для отладки");
                    }
                    //rowNumExcel=activeSheet.getLastRowNum();;  //номер строки по Excel
                    String[] data = rowCsv.split(";");

                    int nppi=0;
                    int i = 0;
                    row=null;
                    for (String di : data) {
                        if (needSaveRow){
                            nppi++;
                            //вычислю hash от лицевого счета
                            if(i==0 )System.out.println("Hash Лиц Счета = " + getHash(di));
                            //тут надо решить записывать ли строку data в HashBd, сверив и TipSnab

                            //если для файла подходит данный лс
                            if(i==0 && getHash(di)==indexHash) {
                                needSaveRow = true;
                                row = activeSheet.createRow(rowNumExcel++);
                            }
                            if(i==0 && getHash(di)!=indexHash) {
                                needSaveRow = false;
                            }

                            //Если TipSnab не подходит то текущую row не нужно записывать в файл
                            if(needSaveRow && nppi == SettingBd.FLI_NameColl.Тип_снабжения.getNumberColl()) {
                                if (nppi == SettingBd.FLI_NameColl.Тип_снабжения.getNumberColl() && !this.tipSnab.valueEquals(di)) {
                                    System.out.println("Выкинули TipSnab = " + di);
                                    activeSheet.removeRow(row);
                                    //Строку удалили созданную. Верну i назад
                                    rowNumExcel--;
                                    needSaveRow = false;
                                }
                            }
                            if(needSaveRow) {   //если понимаем что что row еще нужна в ходе процесса
                                if (row != null) {
                                    cell = row.createCell(i++, CellType.STRING);
                                    //cell = row.createCell(i, CellType.STRING);
                                    //if (rowNum == 0) cell.setCellValue(di);//Шапочка //Но ведь шапочка уже есть ранее при создании файла. Поиск этой ошибочной строки занял 3ч.
                                    if (rowNum > 0 && di.length() > 1)
                                        cell.setCellValue(di.substring(1, di.length() - 1));//Данные
    //                if (rowNum==10000 && rowNum%5000==0){
    //                di.substring(1,di.length()-1);
    //                cell.setCellValue(di);
                                    //System.out.print("<"+ i +"> " + di);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    if (rowNum == toStr) {//Конец Работы. Сохраним файл
                        System.out.println("Загрузка кончилась - начало сохранения данных");
                        if (getFileInputStream()!=null) getFileInputStream().close();
                        //File file = new File(this.getPath());
                        FileOutputStream fileOutputStream = new FileOutputStream(fileExcel);
                        workbook.write(fileOutputStream);
                        fileOutputStream.close();
                        System.out.println("СОХРАНЕНО при rowNum =  " + rowNum);
                        //Закрою открытый вначале CSV
                        bufferedReader.close();
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

        System.out.println("Дошли до конца фала");
        if (getFileInputStream()!=null) getFileInputStream().close();
        FileOutputStream fileOutputStream = new FileOutputStream(fileExcel);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        //Закрою открытый вначале CSV
        bufferedReader.close();
        System.out.println("СОХРАНЕНО т.к. конец файла загрузки. rowNum =  " + rowNum);

//        File file = new File(this.getPath());
//        FileOutputStream fileOutputStream = new FileOutputStream(file);
//        workbook.write(fileOutputStream);
//        fileOutputStream.close();
        return false;
    }

    public void saveParaMap(String key, String nameSheet, HashMap<EnumNameColl,String> what, HashMap<EnumNameColl,String> place) throws IOException {
        //Перед использованием надо задать chatID и nameUser в это Bd
        //Поищу key среди имеющихся записей (key = лицевой счет)
        //1.Если нету - создам строку. 2.Если есть - Добавлю данные к существующей строке
        //
        File file = new File(getPathHashBd_TekIndex());
        FileInputStream fileInputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet activeSheet = workbook.getSheet(nameSheet);

        int columnKey = SettingBd.FLO_NameColl.Лицевой_счет.getNumberColl() - 1;//внутри с 0 нумерация. А Excel в представлении юзеру нумерует с 1
        int columnId = SettingBd.FLO_NameColl.ChatId.getNumberColl() - 1;//внутри с 0 нумерация. А Excel в представлении юзеру нумерует с 1
        //int columnTS = SettingBd.FLO_NameColl.Тип_снабжения.getNumberColl()-1;//внутри с 0 нумерация. А Excel в представлении юзеру нумерует с 1

        String otvet="ничего не найдено!";
        int rowi = 0;//посчитаю количество строк. А потом добавлю новую строку
        for(Row row: activeSheet){
            rowi++;
            if(row.getCell(columnKey)!=null && row.getCell(columnKey).getCellType() == CellType.STRING){
                //Сравню с ключом
                if (row.getCell(columnKey).getStringCellValue().equals(key)){
                    //Значит строка с таким лс уже существует
                    //Надо сверю ChatID юзера, который заносит данные //Если getChatID() совпали(1) и "Тип_снабжения"(2)
                    if (row.getCell(columnId).getStringCellValue().equals(getChatID())){  //стало ВСЕ ОК!!!  ТУТ не находит getChatID()   getBot().getChatId()
                    //if (row.getCell(columnId).getStringCellValue().equals(getChatID()) && row.getCell(columnTS).getStringCellValue(getT)){  // + columnTS
                        //после совпадения Key (ЛС) совпали и ChatID
                        //------------------------------------------------------------------------------------------
                        //Занесу новые показания. Все вошедшие столбцы!!!-------------------------------------------
                        for (Map.Entry<EnumNameColl,String> p1 :what.entrySet()) {
                            //Мы внутри источника данных. Проверим нужны ли эти данные в приемнике
                            for (Map.Entry<EnumNameColl,String> p2 :place.entrySet()) {
                                if(p1.getKey().equals(p2.getKey())){
                                    //Значит в приемнике этот столбец нужен. Тогда запишу данные
                                    System.out.println("ПЕРЕЗАПИСАНЫ данные: " + p2.getKey().toString() + " = " + p1.getValue());
                                    row.getCell(p2.getKey().getNumberColl()-1).setCellValue(p1.getValue());// -1 Потому как в нумерация с 0

                                    //===============================================================================
                                }
                            }
                        }
                        //===========================================================================================
                        //ТЕПЕРЬ МОЖНО сохранять книгу в файл
                        fileInputStream.close();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        workbook.write(fileOutputStream);
                        fileOutputStream.close();
                        System.out.println("Файл сохранен!");
                        return;
                    }
                }
            }

        }
        //Все строки перебрали Дошли до пустой строки и ранее key не нашли
        Row newRow = activeSheet.createRow(rowi); // + новая строка
        //Занесу новые показания. Все вошедшие столбцы!!!-------------------------------------------
        for (Map.Entry<EnumNameColl,String> p1 :what.entrySet()) {
            //Мы внутри источника данных. Проверим нужны ли эти данные в приемнике
            for (Map.Entry<EnumNameColl,String> p2 :place.entrySet()) {
                //System.out.println(p1.getKey() +"-[]-"+ p2.getKey());
                //if(p1.getKey().toString().equals(p2.getKey().toString())){
                if(p1.getKey().equals(p2.getKey())){
                    //Значит в приемнике этот столбец нужен. Тогда запишу данные
                    //System.out.println("Запишу данные <" + p1.getValue() + "> в колонку = " + p2.getKey().getNumberColl());

                    newRow.createCell(p2.getKey().getNumberColl()-1,CellType.STRING).setCellValue(p1.getValue());//-1 т.к. при в Setting +1
                    //newRow.getCell(p2.getKey().getNumberColl()).setCellValue(p1.getValue());
                    //System.out.println("Записаны данные: " + p2.getKey().toString() + " = " + p1.getValue());
                    //===============================================================================
                    break;
                }
            }
        }
        //===========================================================================================
        //ТЕПЕРЬ МОЖНО сохранять книгу в файл
        fileInputStream.close();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        System.out.println("Файл сохранен!");
    }

    public int getHash(String value) {
        //!!! Игнорируем символы \"  !!!
        try {
            value = value.split("\"")[1];
        }catch (Exception e){
            //Ничего не делаем. раз нет \" от оставим value без изменений
        }

        //Удалю знаки " в начале и в конце ()
        //value = value.split("\"")[1];

        if (value.length()>=2){
            try {
//                int i = Integer.parseInt(value.substring(1,2));
//                int j = Integer.parseInt(value.substring(2,3));
                int i = Integer.parseInt(value.substring(0,1));
                int j = Integer.parseInt(value.substring(1,2));
                if(i+j<=9) {
                    return i+j;
                }else{
                    int i2 = Integer.parseInt(String.valueOf(i+j).substring(0,1));//в последнем всегда 0,1
                    int j2 = Integer.parseInt(String.valueOf(i+j).substring(1,2));//и в последнем всегда 1,2
                    return i2+j2;
                }
                //String buk = value.substring(0);
            } catch (Exception e) {
                System.out.println("Формируем сообщение об ошибке в getHashBd для <" + value + ">");
                return 0;
                //throw new MyError("Для определения HashBd нужны только цифры");
                //e.printStackTrace();
            }

        }
        return 0;
    }

    public String[] getIndexHBd() {
        return indexHBd;
    }

    public void setIndexHBd(String[] indexHBd) {
        this.indexHBd = indexHBd;
    }

    public String getPathHashBd_TekIndex() {
        return pathHashBd_TekIndex;
    }

    public void setPathHashBd_TekIndex(int indexHash) {
        //this.pathHashBd_TekIndex = this.getPath().substring(0,this.getPath().lastIndexOf("."))+"_"+indexHash+this.getPath().substring(this.getPath().lastIndexOf("."));//РАботало
        //this.setPath(this.getPathShablon());// - Эта строка привела к Мега косяку. Когда вместо ссылки на реальный файл я начал пихать шаблон (искал сутки. Печально)
        //this.pathHashBd_TekIndex = this.getPath().substring(0,this.getPath().lastIndexOf("."))+"_"+indexHash+this.getPath().substring(this.getPath().lastIndexOf("."));
        this.pathHashBd_TekIndex = this.getPathShablon().substring(0,this.getPathShablon().lastIndexOf("."))+"_"+indexHash+this.getPathShablon().substring(this.getPathShablon().lastIndexOf("."));
        this.setPath(this.pathHashBd_TekIndex);
    }

    public SettingBd.nameFirm getNameFirm() {
        return nameFirm;
    }

    public void setNameFirm(SettingBd.nameFirm nameFirm) {
        this.nameFirm = nameFirm;
    }

}




class MyError extends Exception{
    String message="";
    public MyError(){
        super();
    }
    public MyError(String message){
        super(message);
        this.message=message;
    }

    @Override
    public String toString() {
        return "MyError{" +
                "ОШИБКА='" + message + '\'' +
                '}';
    }
}
