package shedar.mods.ic2.nuclearcontrol.api;


public interface ICardSettingsWrapper
{
    void setInt(String name, Integer value);
    void setLong(String name, Long value);
    void setString(String name, String value);
    void setBoolean(String name, Boolean value);
    void commit();
}
