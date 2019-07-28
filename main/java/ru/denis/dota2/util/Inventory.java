package ru.denis.dota2.util;

import com.github.koraktor.steamcondenser.community.SteamId;
import com.github.koraktor.steamcondenser.community.WebApi;
import com.github.koraktor.steamcondenser.community.dota2.Dota2Inventory;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.community.GameInventory;
import com.github.koraktor.steamcondenser.community.GameItem;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author denis
 */
public class Inventory {
    public static void getInventoryItems () throws SteamCondenserException  {
        SteamId id = SteamId.create(76561198106184535L);
        String ii = id.getNickname();
        long oo = id.getSteamId64();
        WebApi.setApiKey("9594B368889AE857DCD11C181EC9F86");
        
        Dota2Inventory invent = new Dota2Inventory(76561198106184531L,true);
        boolean hh = Dota2Inventory.isCached(570, 76561198106184531L);
    }
}
