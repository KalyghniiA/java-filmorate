package ru.yandex.practicum.filmorate.dao.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventExtractor implements ResultSetExtractor<List<Event>> {

    @Override
    public List<Event> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Event> events = new ArrayList<>();
        while (rs.next()) {
            Event event = new Event(
                    rs.getInt("USER_ID"),
                    rs.getString("EVENT_TYPE"),
                    rs.getString("OPERATION"),
                    rs.getInt("ENTITY_ID")
            );
            event.setEventId(rs.getInt("EVENT_ID"));
            event.setTimestamp(rs.getLong("TIMESTAMP"));
            events.add(event);
        }
        return events;
    }
}
