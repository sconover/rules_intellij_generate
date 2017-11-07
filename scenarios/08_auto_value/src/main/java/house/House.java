package house;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class House {
  public abstract String color();
  public abstract int floors();

  public static House.Builder builder() {
    return new AutoValue_House.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract House.Builder setColor(String color);
    public abstract House.Builder setFloors(int numFloors);

    public abstract House build();
  }
}
