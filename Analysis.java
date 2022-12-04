package com.sp.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;

import static java.time.LocalDate.now;

public class Analysis {
    public enum ObrOut{НайтиДогПоНомеру,НайтиДогПоСтроке,КтоЯ,ЗапомнитьНомерДоговора,НайтиФамилиюПоNДоговора,
                        ЗапомнитьЛицСчет, ПУ_СохранитьГорВоду, ПУ_СохранитьХолВоду, ПУ_СохранитьЭлЭнергию,
                        ЗагрузитьБДФизЛ,ЗагрузитьБДЮрЛ,ВыгрузитьБДФизЛ,ВыгрузитьБДЮрЛ,
                        Ничего};
    //private
    private Update update;
    private SendMessage sMessage;
    private boolean ready = false;
    private String textMessage="";
    private String textMessagePrevious="";
    private Bd bd;
    private String chatId;
    private Bot bot;//Добавил чтоб был доступ до Bot.mindNomDog и mindNomLS
    //private String mindNomDog;

    public Analysis(Update update, Bot bot) {
        this.update = update;
        if (this.update.hasMessage()==true && this.update.getMessage().hasText()) {
            this.textMessagePrevious=this.textMessage;
            this.textMessage = update.getMessage().getText();
            this.bd=bot.getBd();
            this.chatId=bot.getChatId();
            this.bot=bot;
        }
    }

    public boolean isThereLeft (String string){
        if (this.update.hasMessage()==true && this.update.getMessage().hasText()){
            String mainText = this.update.getMessage().getText();
            //System.out.println("Внутри функции isThereLeft. mainText = " + mainText + ".Ищу = " + string);
            if (mainText.length()>=string.length() && mainText.substring(0,string.length()).toLowerCase().equals(string.toLowerCase()) ) return true;
        }
        return false;
    }

    public void makeSendMessage(Dialog dialog) throws IOException, MyError {
        this.sMessage = new SendMessage();// Create a SendMessage object with mandatory fields
        this.sMessage.setChatId(this.getUpdate().getMessage().getChatId().toString());
        this.sMessage.setParseMode("HTML");
//        this.sMessage.
        //this.sMessage.setText;

            //------Если есть Stories, то заменю текст скрин-----------------
        if (dialog.hasStories()) dialog.setTextScreen(dialog.stories.getStory());
        //================================================================
            //------Если нужна внешняя обработка - то выполню ее--------------
        if (dialog.hasObrOut()){
            ObrOut obrOutTek = dialog.getObrOut();
            switch (obrOutTek) {
                case Ничего:
                    break;
                case НайтиДогПоНомеру:
                //if (dialog.getObrOut() == ObrOut.НайтиДогПоНомеру) {
                    System.out.println("------------------------------");
                    System.out.println("<" + this.chatId + "> " + "this.textMessage = " + this.textMessage);
                    System.out.println("=================================");
                    //---Буду искать только если нет такого диалога ()--Цель Искать то что ввел человек. А то бот ищет сам себя(чат хромает)--
                    if (Dialog.findDialog(this.textMessage) == null) {
                        dialog.setTextScreen(bd.find(this.textMessage, "СписокВсех", 1, 3));
//                        dialog.addVarAnswerMap("/start",dialog);
//                        dialog.addVarAnswerMap("/user",dialog);
//                        Dialog.getVarsANSWerMAP().remove("/start");
//                        Dialog.getVarsANSWerMAP().remove("/user");
                    }
                    break;
                    //===конец Поиска============================================
                //}
                case НайтиДогПоСтроке:
                //if (dialog.getObrOut() == ObrOut.НайтиДогПоСтроке) {
                    System.out.println("------------------------------");
                    System.out.println("<" + this.chatId + "> " + "this.textMessage = " + this.textMessage);
                    System.out.println("=================================");
                    //---Буду искать только если нет такого диалога ()--Цель Искать то что ввел человек. А то бот ищет сам себя(чат хромает)--
                    if (Dialog.findDialog(this.textMessage) == null) {
                        //Поиск не работает после поиска по Лицевому счету.
                        //Думаю что объект bd подменяется и портит дело
                        //*-
                        //dialog.setTextScreen(bd.find10(this.textMessage, "СписокВсех", 3, 3));  //Работатет пока не сделать поиск по Лицевому счету
                        dialog.setTextScreen(getBot().getBd().find10(this.textMessage, "СписокВсех", 3, 3));
                    }
                    break;
                    //===конец Поиска============================================
                //}
                case КтоЯ:
                //if (dialog.getObrOut() == ObrOut.КтоЯ) {
                    System.out.println("<" + this.chatId + "> " + "ChatId = " + update.getMessage().getChatId());
                    //System.out.println("FirstName = " + update.getMessage().getContact().getFirstName());
                    //System.out.println("LastName = " + update.getMessage().getContact().getLastName());
                    //System.out.println("PhoneNumber = " + update.getMessage().getContact().getPhoneNumber());
                    //System.out.println("VCard = " + update.getMessage().getContact().getVCard());
                    //System.out.println("UserId = " + update.getMessage().getContact().getUserId());
                    System.out.println("User = " + update.getMessage().getFrom().getUserName());
                    System.out.println("User = " + update.getMessage().getFrom().getId());
                    System.out.println("User FirstName = " + update.getMessage().getFrom().getFirstName());
                    System.out.println("User LastName = " + update.getMessage().getFrom().getLastName());

                    dialog.setTextScreen("В узких кругах вы известны как " + update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName() + "\n" +
                                         "Ваш ChatId = " + update.getMessage().getChatId());

                    //dialog.setTextScreen("Ваше имя = " + update.getMessage().getContact().getFirstName() + "\n" +
                    //                     "Ваш тел. = " + update.getMessage().getContact().getPhoneNumber() + "\n"  );
                    break;
                //}
                case ЗапомнитьНомерДоговора:
                //if (dialog.getObrOut() == ObrOut.ЗапомнитьНомерДоговора) {
                    //Второй заход
                    //Первичный заход
                    //if (bot.getMindNomDog() == null) {
                        bot.setMindNomDog(this.textMessage);
                        //System.out.println("Запомним bot.mindNomDog = " + bot.getMindNomDog());
                        if (Dialog.findDialog(this.textMessage) == null) {
                            dialog.setTextScreen("Введите фамилию Вашего руководителя, указанного в акте приема передачи");
                            dialog.setObrOut(ObrOut.НайтиФамилиюПоNДоговора);
                        }
                    //}
                    break;
                case НайтиФамилиюПоNДоговора:
                    if (bot.getMindNomDog() != null) {
                        //остановился я тут. 17:00
                        System.out.println("Запомненный договор = " + bot.getMindNomDog());
                        System.out.println("Юзер Указал Фамилию = " + this.textMessage);
                        System.out.println("А я по договору нашел фамилию = " + bd.find(bot.getMindNomDog(), "ULI", SettingBd.ULI_NameColl.ДоговорНомерДоговора.getNumberColl(), SettingBd.ULI_NameColl.КонтактноеЛицоФамилия.getNumberColl()));
                        if (this.textMessage.toLowerCase().equals(bd.find(bot.getMindNomDog(), "ULI", SettingBd.ULI_NameColl.ДоговорНомерДоговора.getNumberColl(), SettingBd.ULI_NameColl.КонтактноеЛицоФамилия.getNumberColl()).toLowerCase()     )) {
                            System.out.println("Фамилии совпали");
                            //dialog.setTextScreen("Вы успешно авторизованы");
                            dialog.setTextScreen("Вы в личном кабинете");
                            //dt[30] = this.dialogBot.addVarAnswerMap("Завершить");
                            //dt[30].setTextScreen("Спасибо");
                            //dt[30].addVarAnswerMap("Начать все заново");

                            dialog.addVarAnswerMap("Начать все заново");
                            Dialog.getVarsANSWerMAP().remove("Начать все заново");
                        }
                        else {dialog.setTextScreen("Ошибка авторизации");}
                    }
                    break;
                case ЗагрузитьБДФизЛ:
                    System.out.println("Загрузка ФЛ начинается");
                    HashBd hashBdTek;
                    //Гор вода
                    hashBdTek = bot.getHashBdFLI_Hot();
                    hashBdTek.hashBd_loadCSV("FLI", bot.getPathBd_FLI_Hot_csv());
                    //Хол вода
                    hashBdTek = bot.getHashBdFLI_Cold();
                    hashBdTek.hashBd_loadCSV("FLI", bot.getPathBd_FLI_Hot_csv());
                    hashBdTek.hashBd_loadCSV("FLI", bot.getPathBd_FLI_Cold_csv());
                    //Эл Энергия
                    hashBdTek = bot.getHashBdFLI_ElEn();
                    hashBdTek.hashBd_loadCSV("FLI", bot.getPathBd_FLI_Hot_csv());
                    hashBdTek.hashBd_loadCSV("FLI", bot.getPathBd_FLI_ElEn_csv());

                    //--2--Создам пустые файлы для сохранения новых показаний. База для OUT---------------------------
                    System.out.println("Загрузка ФЛ завершена");
                    System.out.println("Создание шаблонов ФЛ для OUT начинается");
                    //Гор вода
                    hashBdTek = bot.getHashBdFLO_Hot();
                    hashBdTek.hashBd_loadCSV("FLO", bot.getPathBd_FLO_Hot_csv());
                    //Хол вода
                    hashBdTek = bot.getHashBdFLO_Cold();
                    hashBdTek.hashBd_loadCSV("FLO", bot.getPathBd_FLO_Cold_csv());
                    //Эл Энергия
                    hashBdTek = bot.getHashBdFLO_ElEn();
                    hashBdTek.hashBd_loadCSV("FLO", bot.getPathBd_FLO_ElEn_csv());


                    System.out.println("Создание ШАБЛОНОВ ФЛ для OUT завершено");

                    dialog.setTextScreen("Загрузка БДФизЛ завершена");
                    dialog.setObrOut(ObrOut.Ничего);
                    //dialog.addVarAnswerMap()
                    break;

                case ВыгрузитьБДФизЛ:
                    //Перед выгрузкой очищу файлы csv. Чтоб можно было делать вынрузки несколько раз. Ведь hashBd_saveCSV в файлах дописывают инфу и не удаляет ее
                    //создам новые файлы с шапочкой
                    HashBd.createNewFile_SCV(bot.getPathBd_FLO_Hot_csv(),SettingBd.FLO_NameColl_CSV.values());
                    HashBd.createNewFile_SCV(bot.getPathBd_FLO_Cold_csv(),SettingBd.FLO_NameColl_CSV.values());
                    HashBd.createNewFile_SCV(bot.getPathBd_FLO_ElEn_csv(),SettingBd.FLO_NameColl_CSV.values());

                    System.out.println("Выгрузка данных начинается");
                    HashBd hashBdTekOut;
                        //Гор вода
                    hashBdTekOut = bot.getHashBdFLO_Hot();
                    hashBdTekOut.setNameFirm(SettingBd.nameFirm.РЦ_ВТС);
                    hashBdTekOut.hashBd_saveCSV("FLO", bot.getPathBd_FLO_Hot_csv());  //Для ООО РЦ ВТС (Козоброд)
                        //Хол вода
                    hashBdTekOut = bot.getHashBdFLO_Cold();
                    hashBdTekOut.setNameFirm(SettingBd.nameFirm.РЦ_ВТС);
                    hashBdTekOut.hashBd_saveCSV("FLO", bot.getPathBd_FLO_Hot_csv());  //Для РЦ ВТС (Козоброд)
                    hashBdTekOut.setNameFirm(SettingBd.nameFirm.Водоканал);
                    hashBdTekOut.hashBd_saveCSV("FLO", bot.getPathBd_FLO_Cold_csv()); //Для МУП Водоканала
                        //ЭлектроЭнергия
                    hashBdTekOut = bot.getHashBdFLO_ElEn();
                    hashBdTekOut.setNameFirm(SettingBd.nameFirm.РЦ_ВТС);
                    hashBdTekOut.hashBd_saveCSV("FLO", bot.getPathBd_FLO_Hot_csv());  //Для РЦ ВТС (Козоброд)
                    hashBdTekOut.setNameFirm(SettingBd.nameFirm.Электросбыт);
                    hashBdTekOut.hashBd_saveCSV("FLO", bot.getPathBd_FLO_ElEn_csv()); // Для ООО ЭлектроСбыта

                    System.out.println("Выгрузка данных Завершена");

                    //E-Mail
                    System.out.println("Отправка CSV по email");
                    Email email = new Email();
                    email.setAddressTo("sadovoipe@rcvts.ru");
                    email.setTema("Выгрузка БД в CSV файлах");
                    email.setText("Информация во вложении");
                    //System.out.println("Вложение к письму:  bot.getPathBd_FLO_Hot_csv() = " + bot.getPathBd_FLO_Hot_csv());
                    email.addFile(bot.getPathBd_FLO_Hot_csv());
                    email.addFile(bot.getPathBd_FLO_Cold_csv());
                    email.addFile(bot.getPathBd_FLO_ElEn_csv());
                    email.createEmail();
                    email.sendEmail();

                    ////////////////////////////////////////////
                    //kozobrodaa@rcvts.ru
                    //email.setAddressTo("kozobrodaa@rcvts.ru");
                    //email.setAddressTo("vasilenkoss@rcvts.ru");
                    //email.setTema("Выгрузка БД в CSV файлах.");
                    //email.createEmail();
                    //email.sendEmail();
                    /////////////////////////////////////////////

                    System.out.println("E-Mail отправлен");
                    dialog.setTextScreen("Выгрузка прошла успешно. E-mail отправлен на " + email.getAddressTo());
                    dialog.setObrOut(ObrOut.Ничего);
                    break;

                case ЗапомнитьЛицСчет:
                    //Вынесу обработку в отдельную процедуру
                    System.out.println("Запоминаем лицевой счет =" + this.getTextMessage());
                    getBot().setMindNomLS(this.getTextMessage());//Далее проверим что это. может это не л/с ввели
                    System.out.println("в памяти = " + getBot().getMindNomLS());
                    if (Dialog.findDialog(this.textMessage) == null) {
                        String info = "функция которая по ЛС<this.getTextMessage()> найдет инфу";
                        //--------установка БД для поиска л/с---
                        getBot().setHashBdFLI(getBot().getHashBdFLI_Hot());// Пусть л/с ищется в гор воде (там ведь полный перечень всех л/с)
                        //=======================================

                        int hashIndexTek = bot.getHashBdFLI().getHash(this.getTextMessage());
                        bot.getHashBdFLI().setPathHashBd_TekIndex(hashIndexTek); //установка конкретной PathHashBd_TekIndex

                        //System.out.println("Установка дб = " + getBot().getHashBdFLI().getPathHashBd_TekIndex()) ;
                        //System.out.println("getBot().getHashBdFLI().setPath = " + getBot().getHashBdFLI().getPath()) ;
                        //getBot().getHashBdFLI().setPath(getBot().getHashBdFLI().getPathHashBd_TekIndex()); //лишнее удалить
                        //System.out.println("Ваши старые показания = "+ getBot().getBd().find(this.getTextMessage(),"FLI",1,7));

                        //Тут у поиска вместо 1 верну много (Map)
                        //String PU_LastValue = getBot().getHashBdFLI().find(this.getTextMessage(),"FLI",1,7); //ПУ последние показания
                        getBot().setRequest(new HashMap<EnumNameColl, String>());
                        //getBot().setReply(new HashMap<EnumNameColl, String>());
                        HashMap<EnumNameColl,String> replyMapOld = getBot().getRequest();//Заполню replyMapOld. Это ответ для дальнейшей записи в файл (БД)!!!(ТУТ разово можно столбцы без значений)
                        HashMap<EnumNameColl,String> replyMapNew = new HashMap<EnumNameColl, String>();
                        HashMap<EnumNameColl,String> replyMapON = new HashMap<EnumNameColl, String>(); //Old + New
                        //HashMap<EnumNameColl,String> replyMapOld = new HashMap<EnumNameColl, String>(); //Так было до появления Request у bot
                        replyMapOld.put(SettingBd.FLI_NameColl.Лицевой_счет,"");
                            //Сперва для скорости заполню только л/с. И потом уже добавлю остальные поля
                            //replyMapOld = getBot().getHashBdFLI().findParaMap(this.getTextMessage(),"FLI",1, replyMapOld);

                        replyMapOld.put(SettingBd.FLI_NameColl.Улица,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.N_дома,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.N_квартиры,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Старые_показания,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Дата_Последних_Показаний,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.N_счетчика ,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Описание ,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Код_1с ,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Телефон ,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Тип_снабжения ,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Прибор_заблокирован ,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Тариф ,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.База_данных ,"");
                        replyMapOld.put(SettingBd.FLI_NameColl.Дата_поверки ,"");

                        //System.out.println("replyMapOld =" + replyMapOld);
                        //System.out.println("getBot().getRequest() = " + getBot().getRequest());

                        //replyMapOld = getBot().getHashBdFLI().findParaMap(this.getTextMessage(),"FLI",1, replyMapOld); //ПУ заполнение replyMapOld//перенес в case "ПУ_СохранитьГорВоду:"

                        HashSet<EnumNameColl> filterFLI = new HashSet<EnumNameColl>();
                        filterFLI.addAll(Arrays.asList(SettingBd.FLI_NameColl.values()));
                        System.out.println("Set FLI = " + filterFLI);
                        //SettingBd.FLI_NameColl[] filter = SettingBd.FLI_NameColl.values;

                        //Заполню данными replyMapOld, а getBot().getRequest() останется чистая !!!
                        replyMapOld = getBot().getHashBdFLI().findParaMap(getBot().getMindNomLS(),"FLI",1, replyMapOld);
                        replyMapOld = (HashMap<EnumNameColl,String>)replyMapOld.clone(); //В этой строке разорву связь с getBot().getRequest()


                        if(replyMapOld.get(SettingBd.FLI_NameColl.Лицевой_счет)==null){
                            //Если лс=пусто то сюда
                            getBot().setMindNomLS(null);//Сотру в памяти то, что там было. Неверное.
                            dialog.setTextScreen("Такой лицевой счет не найден.\n Отправьте другой.");
                        }else{
                            //тут = значит нашли лс
                            replyMapNew.put(SettingBd.FLO_NameColl.ChatId,getBot().getChatId());
                            replyMapNew.put(SettingBd.FLO_NameColl.Имя_Пользователя,getBot().getFirstName());

                            replyMapON.clear();
                            replyMapON.putAll(replyMapOld);
                            replyMapON.putAll(replyMapNew);
                            //replyMapON.put(SettingBd.FLO_NameColl.ChatId,update.getMessage().getChatId().toString());//!!!
                            //replyMapON.put(SettingBd.FLO_NameColl.Имя_Пользователя,update.getMessage().getFrom().getFirstName());//!!!

//                            String textAnswer = "Здравстуйте " + update.getMessage().getFrom().getFirstName() + ". \n По указанному л/с = " + getBot().getMindNomLS() + ":\n" +
//                                    "Адрес:\n" + replyMapON.get(SettingBd.FLI_NameColl.Улица) + " дом." + replyMapON.get(SettingBd.FLI_NameColl.N_дома) + " кв." + replyMapON.get(SettingBd.FLI_NameColl.N_квартиры) +
//                                    "\n последний раз " + SettingBd.ddMMGGGG(replyMapON.get(SettingBd.FLI_NameColl.Дата_Последних_Показаний)) + " были приняты к учету показания по <b>ГОРЯЧЕЙ ВОДЕ</b> = " + replyMapON.get(SettingBd.FLI_NameColl.Старые_показания);

                            String textAnswer = "Здравстуйте " + update.getMessage().getFrom().getFirstName() + ". \n По указанному л/с " + getBot().getMindNomLS() + ":\n" +
                                    "Адрес:\n" + replyMapON.get(SettingBd.FLI_NameColl.Улица) + " дом." + replyMapON.get(SettingBd.FLI_NameColl.N_дома) + " кв." + replyMapON.get(SettingBd.FLI_NameColl.N_квартиры) +
                                    "\n ";

                            if (replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания).equals("")){
                                textAnswer = textAnswer + "По <b>ГОРЯЧЕЙ ВОДЕ</b> нет данных о предыдущих показаниях счетчиков ";
                            }else{
                                textAnswer = "последний раз " + SettingBd.ddMMGGGG(replyMapON.get(SettingBd.FLI_NameColl.Дата_Последних_Показаний)) + " были приняты к учету показания по <b>ГОРЯЧЕЙ ВОДЕ</b> = " + replyMapON.get(SettingBd.FLI_NameColl.Старые_показания);
                            }

                            textAnswer = textAnswer + "\n Отправьте новые показания";
                            System.out.println(textAnswer);
                            dialog.setTextScreen(textAnswer);
                            dialog.setObrOut(ObrOut.ПУ_СохранитьГорВоду);
                            dialog.addVarAnswerMap("Без изменений");
                            Dialog.getVarsANSWerMAP().remove("Без изменений");// Просто этот текст и это всё что нужно вместо показаний. Обработкаа учтет это.
                        }

                        //dialog.setTextScreen("Введите фамилию Вашего руководителя, указанного в акте приема передачи");
                        //dialog.setObrOut(ObrOut.НайтиФамилиюПоNДоговора);
                        break;
                    }
                case ПУ_СохранитьГорВоду:{
                    bot.carmaAdd();
                    //System.out.println("СТАРТ процесса сохранения показаний по Горячей Воде");
                    String PU_Value_New = null; //PU_Value_New = прибор учета показания новые
                    String PU_Value_Screen = null; //Вместо "." будет "," Для удобства восприятия отделения целой и дробной частей
                    String textAnswer = "Введите показания по Холодной Воде:";//Это переход на след шаг
                    String textAnswer02;
                    getBot().setHashBdFLO( getBot().getHashBdFLO_Hot()); //   !!!   ТУТ установка шаблона файла для сохранения  !!! изм. 1из2  (+горяч на холод по тексту)
                    getBot().setHashBdFLI( getBot().getHashBdFLI_Hot()); //                                                         изм. 2из2
                    if (Dialog.findDialog(this.textMessage) == null) {

                        //ОБЩИЕ------------------------------
                        //Установка файла для сохранения
                        String keyLS = getBot().getMindNomLS();
                        //String keyLS = bot.getRequest().get(SettingBd.FLI_NameColl.Лицевой_счет);
                        getBot().getHashBdFLI().setPathHashBd_TekIndex( getBot().getHashBdFLI().getHash(keyLS));
                        getBot().getHashBdFLO().setPathHashBd_TekIndex( getBot().getHashBdFLO().getHash(keyLS));
                        //System.out.println("файл прежних path = " + this.getBot().getHashBdFLI().getPath());
                        //System.out.println("файл прежних показаний = " + this.getBot().getHashBdFLI().getPathHashBd_TekIndex());//!!!
                        //System.out.println("файл для сохр = " + this.getBot().getHashBdFLO().getPathHashBd_TekIndex());//!!!
                             //заполню ТУТ старые значения, а не при сохр л/с
                        HashMap<EnumNameColl,String> replyMapOld = getBot().getRequest();//Заполню reply. Это уже ответ с old данными для дальнейшей записи в файл (БД)!!!
                        HashMap<EnumNameColl,String> replyMapNew = new HashMap<EnumNameColl, String>();
                        HashMap<EnumNameColl,String> replyMapON = new HashMap<EnumNameColl, String>(); //Old + New

                        replyMapNew.put(SettingBd.FLO_NameColl.ChatId,getBot().getChatId());
                        replyMapNew.put(SettingBd.FLO_NameColl.Имя_Пользователя,getBot().getFirstName());

                        //replyOld = (HashMap<EnumNameColl, String>) replyOld.clone();
                            //System.out.println("Старые значения = " + replyMapOld);
                            //reply = getBot().getHashBdFLI().findParaMap(getBot().getMindNomLS(),"FLI",1, reply); //ПУ заполнение reply//перенес в case "ПУ_СохранитьГорВоду:"
                            //System.out.println("Старые значения гор повторно = " + reply);
                        //---saveParaMap---------------------------------------------------------------------------------------------
                        HashMap<EnumNameColl,String> saveMap = new HashMap<EnumNameColl,String>();
                        //Заполняет saveMap Элементами SettingBd.FLO_NameColl //Это фильтр того что попадет в ответ
                        for (SettingBd.FLO_NameColl floi: SettingBd.FLO_NameColl.values()){
                            saveMap.put(floi,"");
                        }

                        //System.out.println("bot.getRequest()="+bot.getRequest() + "\n saveMap = " + saveMap);//!!!
                        //ОБЩИЕ=======================================================
                        if (this.textMessage.equals("Без изменений")) {
                            //System.out.println("Показания сохраняются без изменений");
                            //String PU_Value_Last = bot.getRequest().get(SettingBd.FLI_NameColl.Старые_показания);
                            String PU_Value_Last = replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания);
                            PU_Value_New = PU_Value_Last;
                            replyMapNew.put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_New);

                            PU_Value_Screen = PU_Value_New.replace(".", ",");
                            textAnswer02 = "Показания по <b>ГОРЯЧЕЙ ВОДЕ</b> сохранены в прежнем объеме. ";
                            //dialog.setTextScreen("Показания по ГОРЯЧЕЙ ВОДЕ сохранены в прежнем объеме. " + PU_Value_Screen + "\n\n" + textAnswer);
                        }else {
                            System.out.println("Идет процесс сохранения новых показаний по Горячей Воде");
                            PU_Value_New = this.textMessage.trim();
                            PU_Value_New = PU_Value_New.replace(",", ".");
                            try {
                                Double valueNEW = Double.parseDouble(PU_Value_New);
                                //ТУТ значит => есть 2 числа(old & new) и их можно сравнить.
                                Double valueOLD = 0.0;
                                if (replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания) != "")
                                    valueOLD = Double.parseDouble(replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания).replace(",", "."));
                                if (valueNEW<0  || valueNEW<valueOLD) {
                                    dialog.setTextScreen("Показания по <b>ГОРЯЧЕЙ ВОДЕ</b> указаны <i><b>ниже предыдущих</b></i> и не будут сохранены. Отправьте повторно\n");
                                    break;
                                }

                                PU_Value_Screen = PU_Value_New.replace(".", ",");
                                replyMapNew.put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_Screen); //Добавлю Новые показания
                                //bot.getRequest().put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_Screen); //Добавлю Новые показания в Request (Новые значения)
                                textAnswer02 = "Показания по ГОРЯЧЕЙ ВОДЕ приняты в объеме = ";
                                //dialog.setTextScreen("Показания по ГОРЯЧЕЙ ВОДЕ приняты в объеме = " + PU_Value_Screen + "\n\n" + textAnswer);
                            }catch (Exception e) {
                                System.out.println("Показания по ГОРЯЧЕЙ ВОДЕ указаны неверно и не будут сохранены. Отправьте повторно\n ОШИБКА = " + e.getMessage());
                                dialog.setTextScreen("Показания по ГОРЯЧЕЙ ВОДЕ указаны неверно и не будут сохранены. Отправьте повторно\n");
                                break;
                            }
                        }
                        //===saveParaMap=============================================================================================
                        //Сохранение данных
                        replyMapNew.put(SettingBd.FLO_NameColl.Дата_подачи, now().toString());//Сохраню дату последних изменений (GGGG-MM-dd)
                        replyMapNew.put(SettingBd.FLO_NameColl.Источник,"telegramBot");

                        replyMapON.clear();
                        replyMapON.putAll(replyMapOld);
                        replyMapON.putAll(replyMapNew);
                            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //System.out.println("Сохраняем файл");
                        //System.out.println("Данные replyMapON = " + replyMapON);
                        this.getBot().getHashBdFLO().setChatID(getBot().getChatId());//Тут Запомню ChatID(), чтобы при сохранении от одного ChatID() перетирать старые его данные
                        this.getBot().getHashBdFLO().saveParaMap(keyLS,"FLO", replyMapON,saveMap);//!!!!!!!!!!!!!!!!!!

                        System.out.println("Файл сохранен = " + getBot().getHashBdFLO().getPath());

                        //////////////////////////////////////////////////////////////////////////////////////////////////////
                        //----------***-------Подготовка данных для Следующего этапа ---------****----------------------------
                        //Для холодной воды
                        //Заполню данными replyMapOld, а getBot().getRequest() останется чистая !!!
                        System.out.println("Подготовка инфы для след этапа (Хол вода)");
                        getBot().setHashBdFLI(getBot().getHashBdFLI_Cold()); //
                        getBot().getHashBdFLI().setPathHashBd_TekIndex( getBot().getHashBdFLI().getHash(keyLS));
                        replyMapOld = getBot().getRequest();
                        replyMapOld = getBot().getHashBdFLI().findParaMap(getBot().getMindNomLS(),"FLI",1, replyMapOld);
                        if(replyMapOld.get(SettingBd.FLI_NameColl.Лицевой_счет)==null){
                            //Тут. Значит по ХолВоде нет данных и их не нужно передавать
                            //Узнаем как дела с электроЭнергией
                            System.out.println("Нет холодной воды. Подготовка инфы для след этапа. (ЭлектроЭнергия)");
                            getBot().setHashBdFLI(getBot().getHashBdFLI_ElEn()); //
                            getBot().getHashBdFLI().setPathHashBd_TekIndex( getBot().getHashBdFLI().getHash(keyLS));
                            replyMapOld = getBot().getRequest();
                            replyMapOld = getBot().getHashBdFLI().findParaMap(getBot().getMindNomLS(),"FLI",1, replyMapOld);
                            if(replyMapOld.get(SettingBd.FLI_NameColl.Лицевой_счет)==null){
                                //Тут. Значит => НЕТ Хол Воды , НЕТ ЭлектроЭнергии
                                //Тогда тут закончим
                                textAnswer="";
                                textAnswer = textAnswer + "\n До встречи в следующем месяце!";
                                dialog.setTextScreen(textAnswer02 + PU_Value_Screen + "\n\n" + textAnswer);
                                dialog.setObrOut(ObrOut.Ничего);
                                dialog.getVarAnswerMap().remove(1);//Удаление кнопки "Без изменений"
                                break;
                            }
                            //Тут. Значит => НЕТ Хол Воды , ЕСТЬ ЭлектроЭнергия
                            System.out.println("Для ЭлектроЭнергии replyMapOld =" + replyMapOld);
                            //replyMapOld = (HashMap<EnumNameColl,String>)replyMapOld.clone(); //В этой строке разорву связь с getBot().getRequest()
                            textAnswer= "";
                            //textAnswer= "последний раз " + SettingBd.ddMMGGGG(replyMapOld.get(SettingBd.FLI_NameColl.Дата_Последних_Показаний)) + " были переданы показания по ЭлектроЭнергии = " + replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания);
                            textAnswer = textAnswer + "\n Отправьте новые показания";
                            //===========***======Подготовка данных для Следующего этапа ==========****===========================
                            dialog.setTextScreen(textAnswer02 + PU_Value_Screen + "\n\n" + textAnswer);
                            dialog.setObrOut(ObrOut.ПУ_СохранитьЭлЭнергию);
                            break;
                        }

                        //System.out.println("Для холодной воды replyMapOld =" + replyMapOld);
                        //replyMapOld = (HashMap<EnumNameColl,String>)replyMapOld.clone(); //В этой строке разорву связь с getBot().getRequest()
                        if (replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания).equals("")){
                            textAnswer = textAnswer + "По <b>Холодной ВОДЕ</b> нет данных о предыдущих показаниях счетчиков ";
                        }else{
                            textAnswer= "последний раз " + SettingBd.ddMMGGGG(replyMapOld.get(SettingBd.FLI_NameColl.Дата_Последних_Показаний)) + " были приняты к учету показания по <b>Холодной ВОДЕ</b> = " + replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания);
                        }

                        textAnswer = textAnswer + "\n Отправьте новые показания";
                        //===========***======Подготовка данных для Следующего этапа ==========****===========================


                        dialog.setTextScreen(textAnswer02 + PU_Value_Screen + "\n\n" + textAnswer);
                        dialog.setObrOut(ObrOut.ПУ_СохранитьХолВоду);
                    }
                    break;
                }
                case ПУ_СохранитьХолВоду:{
                    //System.out.println("СТАРТ процесса сохранения показаний по Холодной Воде");
                    String PU_Value_New = null; //PU_Value_New = прибор учета показания новые
                    String PU_Value_Screen = null; //Вместо "." будет "," Для удобства восприятия отделения целой и дробной частей
                    String textAnswer = "Введите показания по ЭлектроЭнергии:";//Это переход на след шаг
                    String textAnswer02;
                    getBot().setHashBdFLO(getBot().getHashBdFLO_Cold()); //   !!!   ТУТ установка шаблона файла для сохранения  !!! изм. 1из3  (+горяч на холод по тексту далее)
                    getBot().setHashBdFLI(getBot().getHashBdFLI_Cold()); //                                                         изм. 2из3

                    if (Dialog.findDialog(this.textMessage) == null) {

                        //ОБЩИЕ------------------------------
                        //Установка файла для сохранения
                        String keyLS = getBot().getMindNomLS();
                        //String keyLS = bot.getRequest().get(SettingBd.FLI_NameColl.Лицевой_счет);
                        getBot().getHashBdFLI().setPathHashBd_TekIndex( getBot().getHashBdFLI().getHash(keyLS));
                        getBot().getHashBdFLO().setPathHashBd_TekIndex( getBot().getHashBdFLO().getHash(keyLS));
                        //System.out.println("файл прежних path = " + this.getBot().getHashBdFLI().getPath());
                        //System.out.println("файл прежних показаний = " + this.getBot().getHashBdFLI().getPathHashBd_TekIndex());//!!!
                        //System.out.println("файл для сохр = " + this.getBot().getHashBdFLO().getPathHashBd_TekIndex());//!!!
                        //заполню ТУТ старые значения, а не при сохр л/с
                        HashMap<EnumNameColl,String> replyMapOld = getBot().getRequest();//Заполню reply. Это уже ответ с old данными для дальнейшей записи в файл (БД)!!!
                        HashMap<EnumNameColl,String> replyMapNew = new HashMap<EnumNameColl, String>();
                        HashMap<EnumNameColl,String> replyMapON = new HashMap<EnumNameColl, String>(); //Old + New

                        replyMapNew.put(SettingBd.FLO_NameColl.ChatId,getBot().getChatId());
                        replyMapNew.put(SettingBd.FLO_NameColl.Имя_Пользователя,getBot().getFirstName());

                        //replyOld = (HashMap<EnumNameColl, String>) replyOld.clone();
                        //System.out.println("Старые значения = " + replyMapOld);
                        //reply = getBot().getHashBdFLI().findParaMap(getBot().getMindNomLS(),"FLI",1, reply); //ПУ заполнение reply//перенес в case "ПУ_СохранитьГорВоду:"
                        //System.out.println("Старые значения гор повторно = " + reply);
                        //---saveParaMap---------------------------------------------------------------------------------------------
                        HashMap<EnumNameColl,String> saveMap = new HashMap<EnumNameColl,String>();
                        //Заполняет saveMap Элементами SettingBd.FLO_NameColl //Это фильтр того что попадет в ответ
                        for (SettingBd.FLO_NameColl floi: SettingBd.FLO_NameColl.values()){
                            saveMap.put(floi,"");
                        }

                        //System.out.println("bot.getRequest()="+bot.getRequest() + "\n saveMap = " + saveMap);//!!!
                        //ОБЩИЕ=======================================================
                        if (this.textMessage.equals("Без изменений")) {
                            //System.out.println("Показания сохраняются без изменений");
                            //String PU_Value_Last = bot.getRequest().get(SettingBd.FLI_NameColl.Старые_показания);
                            String PU_Value_Last = replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания);
                            PU_Value_New = PU_Value_Last;
                            replyMapNew.put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_New);

                            PU_Value_Screen = PU_Value_New.replace(".", ",");
                            textAnswer02 = "Показания по <b>Холодной ВОДЕ</b> сохранены в прежнем объеме. ";
                            //dialog.setTextScreen("Показания по ГОРЯЧЕЙ ВОДЕ сохранены в прежнем объеме. " + PU_Value_Screen + "\n\n" + textAnswer);
                        }else {
                            System.out.println("Идет процесс сохранения новых показаний по Холодной Воде");
                            PU_Value_New = this.textMessage.trim();
                            PU_Value_New = PU_Value_New.replace(",", ".");
                            try {
                                Double valueNEW = Double.parseDouble(PU_Value_New);
                                //ТУТ значит => есть 2 числа(old & new) и их можно сравнить.
                                Double valueOLD = 0.0;
                                if (replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания) != "")
                                    valueOLD = Double.parseDouble(replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания).replace(",", "."));
                                if (valueNEW<0  || valueNEW<valueOLD) {
                                    dialog.setTextScreen("Показания по Холодной ВОДЕ указаны <i><b>ниже предыдущих</b></i> и не будут сохранены. Отправьте повторно\n");
                                    break;
                                }

                                PU_Value_Screen = PU_Value_New.replace(".", ",");
                                replyMapNew.put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_Screen); //Добавлю Новые показания
                                //bot.getRequest().put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_Screen); //Добавлю Новые показания в Request (Новые значения)
                                textAnswer02 = "Показания по Холодной ВОДЕ приняты в объеме = ";
                                //dialog.setTextScreen("Показания по ГОРЯЧЕЙ ВОДЕ приняты в объеме = " + PU_Value_Screen + "\n\n" + textAnswer);
                            }catch (Exception e) {
                                System.out.println("Показания по Холодной ВОДЕ указаны неверно и не будут сохранены. Отправьте повторно\n ОШИБКА = " + e.getMessage());
                                dialog.setTextScreen("Показания по Холодной ВОДЕ указаны неверно и не будут сохранены. Отправьте повторно\n");
                                break;
                            }
                        }
                        //===saveParaMap=============================================================================================
                        //Сохранение данных
                        replyMapNew.put(SettingBd.FLO_NameColl.Дата_подачи, now().toString());//Сохраню дату последних изменений (GGGG-MM-dd)
                        replyMapNew.put(SettingBd.FLO_NameColl.Источник,"telegramBot");

                        replyMapON.clear();
                        replyMapON.putAll(replyMapOld);
                        replyMapON.putAll(replyMapNew);
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //System.out.println("Сохраняем файл");
                        //System.out.println("Данные replyMapON = " + replyMapON);
                        this.getBot().getHashBdFLO().setChatID(getBot().getChatId());//Тут Запомню ChatID(), чтобы при сохранении от одного ChatID() перетирать старые его данные
                        this.getBot().getHashBdFLO().saveParaMap(keyLS,"FLO", replyMapON,saveMap);//!!!!!!!!!!!!!!!!!!
                        System.out.println("Файл сохранен = " + getBot().getHashBdFLO().getPath());

                        //----------***-------Подготовка данных для Следующего этапа ---------****----------------------------
                        //Для ЭлектроЭнергии
                        //Заполню данными replyMapOld, а getBot().getRequest() останется чистая !!!
                        System.out.println("Подготовка инфы для след этапа (ЭлектроЭнергия)");
                        getBot().setHashBdFLI(getBot().getHashBdFLI_ElEn());             //                                              это изм. 3из3
                        getBot().getHashBdFLI().setPathHashBd_TekIndex( getBot().getHashBdFLI().getHash(keyLS));
                        replyMapOld = getBot().getRequest();
                        replyMapOld = getBot().getHashBdFLI().findParaMap(getBot().getMindNomLS(),"FLI",1, replyMapOld);

                        if(replyMapOld.get(SettingBd.FLI_NameColl.Лицевой_счет)==null){
                            //Тут. Значит по ЭлектроЭнергии нет данных и их не нужно передавать
                            textAnswer="";
                            textAnswer = textAnswer + "\n До встречи в следующем месяце!";
                            dialog.setTextScreen(textAnswer02 + PU_Value_Screen + "\n\n" + textAnswer);
                            dialog.setObrOut(ObrOut.Ничего);
                            dialog.getVarAnswerMap().remove(1);//Удаление кнопки "Без изменений"
                            break;
                        }

                        System.out.println("Для ЭлектроЭнергии replyMapOld =" + replyMapOld);
                        //replyMapOld = (HashMap<EnumNameColl,String>)replyMapOld.clone(); //В этой строке разорву связь с getBot().getRequest()
                        //textAnswer= "";
                        if (replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания).equals("")){
                            textAnswer = "По <b>ЭлектроЭнергии</b> нет данных о предыдущих показаниях счетчиков ";
                        }else {
                            textAnswer = "последний раз " + SettingBd.ddMMGGGG(replyMapOld.get(SettingBd.FLI_NameColl.Дата_Последних_Показаний)) + " были приняты к учету показания по <b>ЭлектроЭнергии</b> = " + replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания);
                        }
                        textAnswer = textAnswer + "\n Отправьте новые показания";
                        //===========***======Подготовка данных для Следующего этапа ==========****===========================
                        dialog.setTextScreen(textAnswer02 + PU_Value_Screen + "\n\n" + textAnswer);
                        dialog.setObrOut(ObrOut.ПУ_СохранитьЭлЭнергию);
                    }
                    break;
                }
                case ПУ_СохранитьЭлЭнергию:{
                    //System.out.println("СТАРТ процесса сохранения показаний по олодной Воде");
                    String PU_Value_New = null; //PU_Value_New = прибор учета показания новые
                    String PU_Value_Screen = null; //Вместо "." будет "," Для удобства восприятия отделения целой и дробной частей
                    String textAnswer = "Все показания успешно приняты. Спасибо.";//Это переход на след шаг
                    String textAnswer02;
                    getBot().setHashBdFLO(getBot().getHashBdFLO_ElEn()); //   !!!   ТУТ установка шаблона файла для сохранения  !!! изм. 1из3  (+горяч на холод по тексту далее)
                    getBot().setHashBdFLI(getBot().getHashBdFLI_ElEn()); //                                                         изм. 2из3

                    if (Dialog.findDialog(this.textMessage) == null) {

                        //ОБЩИЕ------------------------------
                        //Установка файла для сохранения
                        String keyLS = getBot().getMindNomLS();
                        //String keyLS = bot.getRequest().get(SettingBd.FLI_NameColl.Лицевой_счет);
                        getBot().getHashBdFLI().setPathHashBd_TekIndex( getBot().getHashBdFLI().getHash(keyLS));
                        getBot().getHashBdFLO().setPathHashBd_TekIndex( getBot().getHashBdFLO().getHash(keyLS));
                        //System.out.println("файл прежних path = " + this.getBot().getHashBdFLI().getPath());
                        //System.out.println("файл прежних показаний = " + this.getBot().getHashBdFLI().getPathHashBd_TekIndex());//!!!
                        //System.out.println("файл для сохр = " + this.getBot().getHashBdFLO().getPathHashBd_TekIndex());//!!!
                        //заполню ТУТ старые значения, а не при сохр л/с
                        HashMap<EnumNameColl,String> replyMapOld = getBot().getRequest();//Заполню reply. Это уже ответ с old данными для дальнейшей записи в файл (БД)!!!
                        HashMap<EnumNameColl,String> replyMapNew = new HashMap<EnumNameColl, String>();
                        HashMap<EnumNameColl,String> replyMapON = new HashMap<EnumNameColl, String>(); //Old + New

                        replyMapNew.put(SettingBd.FLO_NameColl.ChatId,getBot().getChatId());
                        replyMapNew.put(SettingBd.FLO_NameColl.Имя_Пользователя,getBot().getFirstName());

                        //replyOld = (HashMap<EnumNameColl, String>) replyOld.clone();
                        //System.out.println("Старые значения = " + replyMapOld);
                        //reply = getBot().getHashBdFLI().findParaMap(getBot().getMindNomLS(),"FLI",1, reply); //ПУ заполнение reply//перенес в case "ПУ_СохранитьГорВоду:"
                        //System.out.println("Старые значения гор повторно = " + reply);
                        //---saveParaMap---------------------------------------------------------------------------------------------
                        HashMap<EnumNameColl,String> saveMap = new HashMap<EnumNameColl,String>();
                        //Заполняет saveMap Элементами SettingBd.FLO_NameColl //Это фильтр того что попадет в ответ
                        for (SettingBd.FLO_NameColl floi: SettingBd.FLO_NameColl.values()){
                            saveMap.put(floi,"");
                        }

                        //System.out.println("bot.getRequest()="+bot.getRequest() + "\n saveMap = " + saveMap);//!!!
                        //ОБЩИЕ=======================================================
                        if (this.textMessage.equals("Без изменений")) {
                            //System.out.println("Показания сохраняются без изменений");
                            //String PU_Value_Last = bot.getRequest().get(SettingBd.FLI_NameColl.Старые_показания);
                            String PU_Value_Last = replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания);
                            PU_Value_New = PU_Value_Last;
                            replyMapNew.put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_New);

                            PU_Value_Screen = PU_Value_New.replace(".", ",");
                            textAnswer02 = "Показания по ЭлектроЭнергии сохранены в прежнем объеме. ";
                            //dialog.setTextScreen("Показания по ЭлектроЭнергии сохранены в прежнем объеме. " + PU_Value_Screen + "\n\n" + textAnswer);
                        }else {
                            System.out.println("Идет процесс сохранения новых показаний по ЭлектроЭнергии");
                            PU_Value_New = this.textMessage.trim();
                            PU_Value_New = PU_Value_New.replace(",", ".");
                            try {
                                Double valueNEW = Double.parseDouble(PU_Value_New);
                                //ТУТ значит => есть 2 числа(old & new) и их можно сравнить.
                                Double valueOLD = 0.0;
                                if (replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания) != "")
                                    valueOLD = Double.parseDouble(replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания).replace(",", "."));
                                if (valueNEW<0  || valueNEW<valueOLD) {
                                    dialog.setTextScreen("Показания по ЭлектроЭнергии указаны <i><b>ниже предыдущих</b></i> и не будут сохранены. Отправьте повторно\n");
                                    break;
                                }

                                PU_Value_Screen = PU_Value_New.replace(".", ",");
                                replyMapNew.put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_Screen); //Добавлю Новые показания
                                //bot.getRequest().put(SettingBd.FLO_NameColl.Новые_показания, PU_Value_Screen); //Добавлю Новые показания в Request (Новые значения)
                                textAnswer02 = "Показания по ЭлектроЭнергии приняты в объеме = ";
                                //dialog.setTextScreen("Показания по ЭлектроЭнергии приняты в объеме = " + PU_Value_Screen + "\n\n" + textAnswer);
                            }catch (Exception e) {
                                System.out.println("Показания по ЭлектроЭнергии указаны неверно и не будут сохранены. Отправьте повторно\n ОШИБКА = " + e.getMessage());
                                dialog.setTextScreen("Показания по ЭлектроЭнергии указаны неверно и не будут сохранены. Отправьте повторно\n");
                                break;
                            }
                        }
                        //===saveParaMap=============================================================================================
                        //Сохранение данных
                        replyMapNew.put(SettingBd.FLO_NameColl.Дата_подачи, now().toString());//Сохраню дату последних изменений (GGGG-MM-dd)
                        replyMapNew.put(SettingBd.FLO_NameColl.Источник,"telegramBot");

                        replyMapON.clear();
                        replyMapON.putAll(replyMapOld);
                        replyMapON.putAll(replyMapNew);
                        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //System.out.println("Сохраняем файл");
                        //System.out.println("Данные replyMapON = " + replyMapON);
                        this.getBot().getHashBdFLO().setChatID(getBot().getChatId());//Тут Запомню ChatID(), чтобы при сохранении от одного ChatID() перетирать старые его данные
                        this.getBot().getHashBdFLO().saveParaMap(keyLS,"FLO", replyMapON,saveMap);//!!!!!!!!!!!!!!!!!!
                        System.out.println("Файл сохранен = " + getBot().getHashBdFLO().getPath());

                        //----------***-------Подготовка данных для Следующего этапа ---------****----------------------------
                        //Для холодной воды
                        //Заполню данными replyMapOld, а getBot().getRequest() останется чистая !!!
                        System.out.println("Подготовка инфы для след этапа (Конец приема показаний)");// может выдать инфу о сроках замены счетчиков?

                        getBot().setHashBdFLI(getBot().getHashBdFLI_ElEn()); //                                                             изм. 3из3
                        getBot().getHashBdFLI().setPathHashBd_TekIndex( getBot().getHashBdFLI().getHash(keyLS));
                        replyMapOld = getBot().getRequest();
                        replyMapOld = getBot().getHashBdFLI().findParaMap(getBot().getMindNomLS(),"FLI",1, replyMapOld);
                        //System.out.println("Для холодной воды replyMapOld =" + replyMapOld);
                        //replyMapOld = (HashMap<EnumNameColl,String>)replyMapOld.clone(); //В этой строке разорву связь с getBot().getRequest()
                        //textAnswer= "";
                        //textAnswer= "последний раз " + SettingBd.ddMMGGGG(replyMapOld.get(SettingBd.FLI_NameColl.Дата_Последних_Показаний)) + " были переданы показания по ЭлектроЭнергии = " + replyMapOld.get(SettingBd.FLI_NameColl.Старые_показания);
                        //textAnswer = textAnswer + "\n Отправьте новые показания";
                        textAnswer="";
                        textAnswer = textAnswer + "\n До встречи в следующем месяце!";
                        //===========***======Подготовка данных для Следующего этапа ==========****===========================

                        dialog.setTextScreen(textAnswer02 + PU_Value_Screen + "\n\n" + textAnswer);
                        dialog.setObrOut(ObrOut.Ничего);
                        dialog.getVarAnswerMap().remove(1);//Удаление кнопки "Без изменений"
                    }
                    break;
                }
            }

        }
        //================================================================

        this.sMessage.setText(dialog.textScreen);
        this.setReady(true);
        int vars = dialog.getVarAnswerMap().size();
        if(vars==0){
            ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
            replyKeyboardRemove.setRemoveKeyboard(true);
            sMessage.setReplyMarkup(replyKeyboardRemove);
        }
        if(vars>0) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            sMessage.setReplyMarkup(replyKeyboardMarkup);
                replyKeyboardMarkup.setSelective(true);//было
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setOneTimeKeyboard(true); //разово
                //replyKeyboardMarkup.setOneTimeKeyboard(false); //много
                List<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
            for (int i = 0; i < vars; i++) {
                //keyboardRow1.add(new KeyboardButton(dialog.getVarAnswerMap().get(i))); //отлично пашет и без WebApp
                KeyboardButton keyboardButton = new KeyboardButton(dialog.getVarAnswerMap().get(i));
                if(dialog.getVarAnswerMap().get(i).equals("я ЮР.Лицо")) {
                    WebAppInfo webAppInfo = new WebAppInfo();
                    webAppInfo.setUrl("https://komdiv.github.io/VTSWaterHot/index.html");
                    keyboardButton.setWebApp(webAppInfo);
                }
                keyboardRow1.add(keyboardButton);
            }
                keyboardRows.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboardRows);
        }
//            //keyboardRow1.add(new KeyboardButton("best"));
//            keyboardRow1.add(new KeyboardButton("■ ■ история(не партии)■ ■"));
//            keyboardRow1.add(new KeyboardButton("Заявление на Замену Счетчика"));
//            keyboardRows.add(keyboardRow1);
//            KeyboardRow keyboardRow2 = new KeyboardRow();
//            keyboardRow2.add(new KeyboardButton("Показания ИПУ"));
//            keyboardRows.add(keyboardRow2);

                }


    //System.out.println("дошли до конца ф-ции makeSendMessage");
    //
    //void


    //----------------- SET  GET ------------------------------------------------------------


    public Bot getBot() {
        return bot;
    }

    public String getTextMessage() {
        return this.textMessage;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    public Update getUpdate() {
        return update;
    }

    public SendMessage getsMessage() {
        return sMessage;
    }
}
