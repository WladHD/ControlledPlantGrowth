package de.wladtheninja.controlledplantgrowth.setup;

import de.wladtheninja.controlledplantgrowth.data.utils.DatabaseHibernateUtil;

public class SetupDatabase implements Runnable {
    @Override
    public void run() {
        DatabaseHibernateUtil.getInstance().setup();
    }
}
