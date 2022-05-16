package ac.misohiyoko.navigatorCom

import java.util.StringJoiner

object TextsLang {
    private var lang: Language = Language.English;

    private val textsList:Map<String, MultiLangText> = mutableMapOf(
        "Destination" to MultiLangText(mutableMapOf(Language.English to "Destination", Language.Japanese to "目的地"))
    )



    public fun setLanguage(lang: Language){
        TextsLang.lang = lang
    }

    public fun getText(text:String):String{
        val text = textsList[text]
        if(text != null){
            return text.getLang(lang)
        }else{
            return "TextNotFound"
        }
    }



}

class MultiLangText(val textMap:Map<Language, String>){
    public fun getLang(lang: Language) : String{
        val content = this.textMap[lang]
        if(content != null){
            return content;
        }else{
            val englsihCon =  this.textMap[Language.English]
            if(englsihCon != null){
                return englsihCon
            }else{
                return "TextNotFound"
            }
        }
    }
}

enum class Language{
    Japanese,English
}