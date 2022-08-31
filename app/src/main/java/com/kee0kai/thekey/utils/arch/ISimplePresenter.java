package com.kee0kai.thekey.utils.arch;

public interface ISimplePresenter {


    /**
     * Получение кэшей состояния view по ключу.
     */
    Object getState(String stateId);

    /**
     * Сохранение состояния view по ключу
     * Все кеши храняться по weak ссылкам, и будут утеряны при первом же сборе мусора
     */
    void saveState(String stateId, Object state);


    /**
     * Подписываемся на изменения данных
     */
    void subscribe(IRefreshView view);

    /**
     * Отписываемся от данных
     */
    void unsubscribe(IRefreshView view);

}
