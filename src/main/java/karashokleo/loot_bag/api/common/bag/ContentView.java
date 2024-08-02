package karashokleo.loot_bag.api.common.bag;

import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;

import java.util.List;

public interface ContentView
{
    List<ContentEntry> getContents();
}
