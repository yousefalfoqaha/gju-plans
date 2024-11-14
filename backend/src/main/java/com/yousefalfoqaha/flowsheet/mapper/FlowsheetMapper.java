package com.yousefalfoqaha.flowsheet.mapper;

import com.yousefalfoqaha.flowsheet.dto.CourseDTO;
import com.yousefalfoqaha.flowsheet.dto.FlowsheetDTO;
import com.yousefalfoqaha.flowsheet.model.Course;
import com.yousefalfoqaha.flowsheet.model.Flowsheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class FlowsheetMapper implements Function<Flowsheet, FlowsheetDTO> {
    private final CourseMapper courseMapper;
    private final SemesterMapper semesterMapper;

    @Autowired
    public FlowsheetMapper(CourseMapper courseMapper, SemesterMapper semesterMapper) {
        this.courseMapper = courseMapper;
        this.semesterMapper = semesterMapper;
    }

    @Override
    public FlowsheetDTO apply(Flowsheet f) {
        Map<Long, Long> courseMappings = new HashMap<>();
        List<CourseDTO> courses = new ArrayList<>();

        f.getCourseMappings().forEach(cm -> {
            Course course = cm.getCourse();
            courses.add(courseMapper.apply(course));
            courseMappings.put(
                    course.getId(),
                    cm.getSemester().getId()
            );
        });

        return new FlowsheetDTO(
                f.getUuid(),
                f.getStudyPlan().getMajor() + " Flowsheet",
                f.getStudyPlan().getSemesters()
                        .stream()
                        .map(semesterMapper)
                        .toList(),
                courseMappings,
                courses
        );
    }
}