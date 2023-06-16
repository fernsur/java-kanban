package server;

public enum Endpoint {
    GET_TASKS, // получение задач по приоритету
    GET_HISTORY, // получение истории
    GET_SUBTASK_EPIC, // получение подзадач для эпика

    GET_ALL_TASKS, // получение всех задач
    GET_TASK, // получение задачи по айди
    POST_TASK, // создание или обновление задачи
    DELETE_ALL_TASKS, // удаление всех задач
    DELETE_TASK, // удаление задачи по айди

    GET_ALL_EPICS, // получение всех эпиков
    GET_EPIC, // получение эпика по айди
    POST_EPIC, // создание или обновление эпика
    DELETE_ALL_EPICS, // удаление всех эпиков
    DELETE_EPIC, // удаление эпика по айди

    GET_ALL_SUBTASKS, // получение всех подзадач
    GET_SUBTASK, // получение подзадачи по айди
    POST_SUBTASK, // создание или обновление подзадачи
    DELETE_ALL_SUBTASKS, // удаление всех подзадач
    DELETE_SUBTASK, // удаление подзадачи по айди

    UNKNOWN
}
