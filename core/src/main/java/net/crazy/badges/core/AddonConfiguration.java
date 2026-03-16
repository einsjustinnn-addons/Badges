package net.crazy.badges.core;

import net.crazy.badges.core.ui.activity.BadgeActivity;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.ActivitySettingWidget.ActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.MethodOrder;

@ConfigName("settings")
public class AddonConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> showOwn = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> compactBadges = new ConfigProperty<>(false);

  @SliderSetting(min = 1, max = 10)
  private final ConfigProperty<Integer> size = new ConfigProperty<>(2);

  @ActivitySetting
  @MethodOrder(after = "size")
  public Activity badgesOverview() {
    return new BadgeActivity();
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public ConfigProperty<Boolean> showOwn() {
    return this.showOwn;
  }

  public ConfigProperty<Boolean> compactBadges() {
    return this.compactBadges;
  }

  public ConfigProperty<Integer> size() {
    return this.size;
  }
}
