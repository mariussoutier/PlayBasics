package extensions;

import play.*;
import play.mvc.*;
import static play.mvc.Results.*;
import play.i18n.*;
import play.libs.F.Option;

import java.util.*;

public class LangQueryWrapper implements QueryStringBindable<LangQueryWrapper> {

  public Lang lang;
  private boolean queryString;

  public LangQueryWrapper() {
  }

  public LangQueryWrapper(Lang lang) {
    this.lang = lang;
  }

  public LangQueryWrapper(play.api.i18n.Lang lang) {
    this.lang = new Lang(lang);
  }

  public Option<LangQueryWrapper> bind(String key, Map<String, String[]> data) {
    this.queryString = true;
    String[] values = data.get(key);
    String value = values[0];
    if (value != null) {
      if (value.length() == 2) {
        this.lang = new Lang(new play.api.i18n.Lang(value, null));
        return Option.Some(this);
      } else if (value.length() == 5) {
        String[] components = value.split("-");
        this.lang = new Lang(new play.api.i18n.Lang(components[0], components[1]));
        return Option.Some(this);
      }
    }

    return Option.None();
  }

  public String unbind(String key) {
    return key + "=" + lang.code();
  }

  public String javascriptUnbind() {
    return lang.code();
  }
}
