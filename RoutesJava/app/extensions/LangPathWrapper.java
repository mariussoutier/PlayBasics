package extensions;

import play.*;
import play.mvc.*;
import static play.mvc.Results.*;
import play.i18n.*;
import play.libs.F.Option;

import java.util.*;

public class LangPathWrapper implements PathBindable<LangPathWrapper> {

  public Lang lang;
  private boolean queryString;

  public LangPathWrapper() {
  }

  public LangPathWrapper(Lang lang) {
    this.lang = lang;
  }

  public LangPathWrapper(play.api.i18n.Lang lang) {
    this.lang = new Lang(lang);
  }

  public LangPathWrapper bind(String key, String value) {
    System.out.println("jo");
    if (value != null) {
      if (value.length() == 2) {
        this.lang = new Lang(new play.api.i18n.Lang(value, null));
        return this;
      } else if (value.length() == 5) {
        String[] components = value.split("-");
        this.lang = new Lang(new play.api.i18n.Lang(components[0], components[1]));
        return this;
      }
    }
    return null;
  }

  public String unbind(String key) {
    return lang.code();
  }

  public String javascriptUnbind() {
    return lang.code();
  }
}
