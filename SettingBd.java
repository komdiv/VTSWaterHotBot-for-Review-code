package com.sp.bot;

import java.util.HashMap;
import java.util.HashSet;

public class SettingBd {

    public enum ModeBot {Пользователь ,ФизЛицо, ЮрЛицо, Сотрудник, Админ;

    }



    //Перечисления для характеристики БД для возможности деления данных по Тип Снабжения
    public enum TipSnab {HotWater, ColdWater, ElEn, Otop;
        private int value;
        TipSnab(){
            this.value = this.ordinal()+1;
            String e = String.valueOf(this.value);
        }

        public String valueToString() {
            return String.valueOf(this.value);
        }

        public boolean valueEquals(String ob){
            try {
                int obrVal = Integer.parseInt(ob.replace("\"",""));// Уберу много лишних "
                if (value==obrVal) return true;
            }catch (Exception e){
                return false;
            }
            //if (valueToString().equals(ob)) return true;
            return false;
        }
    }

    public enum nameFirm{РЦ_ВТС, Водоканал, Электросбыт;
        private int valueInt;
        private String valueStrBd;
        nameFirm(){
            valueInt = this.ordinal()+1;
            switch (valueInt){
                case 1:
                    valueStrBd = "VDN-RCVTS";
                    break;
                case 2:
                    valueStrBd = "РЦ ВТС";
                    break;
                case 3:
                    valueStrBd = "TNS-energo";
                    break;
            }
        }
        public boolean valueStrBdEquals(String nameFirm){
            return valueStrBd.equals(nameFirm);
        }
    }

    //Струтура полей файла по ГОРЯЧЕЙ воде
    public enum FLI_NameColl implements EnumNameColl{Лицевой_счет,Улица,N_дома,N_квартиры,N_счетчика,Описание,Старые_показания,Код_1с,Телефон,Тип_снабжения,Прибор_заблокирован,Тариф,База_данных,Дата_поверки,Дата_Последних_Показаний;
        private int numberColl;
        private int numberRow;
        FLI_NameColl(){
            this.numberColl=this.ordinal() + 1;
            this.numberRow = 2;
        }
//        public String getValueByNameAsStr(String nameAsStr){
//        }
        //---------------Functions----------------------
        @Override
        public boolean equals(EnumNameColl ob){
            if(ob==null) return false;
            if(this.toString().equals(ob.toString())) return  true;
            return false;
        }

        //------GET   SET
        public int getNumberColl() {
            return numberColl;
        }

        public int getNumberRow() {
            return numberRow;
        }
    };
    public enum ULI_NameColl implements EnumNameColl{КонтактноеЛицоФамилия,ДоговорКонтрагент,ДоговорНомерДоговора,ОбъектПотребленияКод,ОбъектПотребления,Здание,ЗначениеЛС,ЗначениеКЖ;
        private int numberColl;
        private int numberRow=2;
        ULI_NameColl(){
            this.numberColl=this.ordinal() + 1;
        }
        //---------------Functions----------------------
        @Override
        public boolean equals(EnumNameColl ob){
            if(ob==null) return false;
            if(this.toString().equals(ob.toString())) return  true;
            return false;
        }

        //------GET   SET
        public int getNumberColl() {
            return numberColl;
        }

        public int getNumberRow() {
            return numberRow;
        }
    };

    //Файл tools.xlsx - настройки для первичной загрузки
    public enum Tools{Путь_к_БД_ФизЛ,Путь_к_БД_ЮрЛ;
        private int numberColl;
        private int numberRow=2;
        Tools(){
            this.numberColl=this.ordinal() + 1;
        }
        //------GET   SET
        public int getNumberColl() {
            return numberColl;
        }

        public int getNumberRow() {
            return numberRow;
        }
    };
////////////////////////////////////////////////////////-------------OUT------------------////////////////////////////////////////////////////////////////
    //Искусственно создам новую колонку для ChatID. Так можно отслеживать кто из юзеров вносит изменения.
    public enum FLO_NameColl implements EnumNameColl {
    ChatId, Имя_Пользователя, Лицевой_счет, Улица, N_дома, N_квартиры, N_счетчика, Описание, Старые_показания, Новые_показания, Дата_подачи, Код_1с, Телефон, Тип_снабжения, Прибор_заблокирован, Тариф, База_данных, Дата_поверки, Источник, Email;
    private int numberColl;
    private int numberRow;

    FLO_NameColl() {
        this.numberColl = this.ordinal() + 1;// не убирать +1. Должно быть едино всё. Либо везде убирать тогда
        //this.numberColl=this.ordinal()+1; //убрал +1. Так как вставка данных идет с 0 позиции (проверю верхнее утверждение).
        this.numberRow = 2;
    }

    //---------------Functions----------------------
    @Override
    public boolean equals(EnumNameColl ob) {
        if (ob == null) return false;
        if (this.toString().equals(ob.toString())) return true;
        return false;
    }

    @Override
    public int getNumberColl() {
        return numberColl;
    }

    @Override
    public int getNumberRow() {
        return numberRow;
    }
}

    //Тут ничего лишнего - только то что попадает в OUT CSV file
    public enum FLO_NameColl_CSV implements EnumNameColl{Лицевой_счет,Улица,N_дома,N_квартиры,N_счетчика,Описание,Старые_показания,Новые_показания,Дата_подачи,Код_1с,Телефон,Тип_снабжения,Прибор_заблокирован,Тариф,База_данных,Дата_поверки,Источник,Email;
        private int numberColl;
        private int numberRow;
        FLO_NameColl_CSV(){
            this.numberColl=this.ordinal()+1;// не убирать +1. Должно быть едино всё. Либо везде убирать тогда
            //this.numberColl=this.ordinal()+1; //убрал +1. Так как вставка данных идет с 0 позиции (проверю верхнее утверждение).
            this.numberRow = 2;
        }

    //---------------Functions----------------------
    @Override
    public boolean equals(EnumNameColl ob){
        if(ob==null) return false;
        if(this.toString().equals(ob.toString())) return  true;
        return false;
    }
    //===============Functions======================
    //------GET   SET
    public int getNumberColl() {
        return numberColl;
    }

    public int getNumberRow() {
        return numberRow;
    }
};

    //===================================================================OUT=================================================================================
    //==========================================================================================================================================================
    //Вспомогательные функции для данных в БД. Пока 1 - то пусть тут лежит
    //Для перевода даты из GGGG-MM-DD to DD.MM.GGGG
    public static String ddMMGGGG(String GGGGMMdd){
        String[] ns = GGGGMMdd.split("-");
        if (ns.length==3) return ns[2]+"."+ns[1]+"."+ns[0];
        return "";
    }

}