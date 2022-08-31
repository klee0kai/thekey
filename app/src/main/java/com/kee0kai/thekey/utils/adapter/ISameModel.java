package com.kee0kai.thekey.utils.adapter;

public interface ISameModel {


    /**
     * Обе модели описывают одну и туже сущность, но при этом могут различаться. Если только данные устарели.
     * К примеру покрасили забор, но обе модели описывают один и тот же забор.
     *
     * @param ob
     * @return
     */
    boolean isSame(Object ob);

}
