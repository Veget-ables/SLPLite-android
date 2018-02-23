package ac.slpl.slplite.database.config;

// リフレクション時に,DB操作に悪影響を与える除外するべきパラメータ
public class ExcludeParameter {
    public final static String PARCELABLE = "CREATOR";               // Parcelable使用時
    public final static String INSTANT_RUN_UID = "serialVersionUID"; // Instant Run有効時
    public final static String INSTANT_RUN_CHANGE = "$change";       // Instant RUn有効時
}
