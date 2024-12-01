package com.yousefalfoqaha.gjuplans.studyplan;

import com.yousefalfoqaha.gjuplans.course.CourseService;
import com.yousefalfoqaha.gjuplans.studyplan.dto.response.SectionResponse;
import com.yousefalfoqaha.gjuplans.studyplan.dto.response.StudyPlanResponse;
import com.yousefalfoqaha.gjuplans.studyplan.exception.StudyPlanNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyPlanService {
    private final StudyPlanRepository studyPlanRepository;
    private final CourseService courseService;

    @Autowired
    public StudyPlanService(StudyPlanRepository studyPlanRepository, CourseService courseService) {
        this.studyPlanRepository = studyPlanRepository;
        this.courseService = courseService;
    }

    public StudyPlanResponse getStudyPlan(long studyPlanId) {
        var studyPlan = studyPlanRepository.findById(studyPlanId)
                .orElseThrow(() -> new StudyPlanNotFoundException(
                        "Study plan with id " + studyPlanId + " was not found."
                ));

        return new StudyPlanResponse(
                studyPlan.getId(),
                studyPlan.getTrack(),
                studyPlan.getStartAcademicYear(),
                studyPlan.getDuration(),
                studyPlan.getProgram().getId(),
                studyPlan.getSections()
                        .stream()
                        .map(sec -> new SectionResponse(
                                sec.getId(),
                                sec.getLevel(),
                                sec.getType(),
                                sec.getName(),
                                sec.getCourses()
                                        .stream()
                                        .map(c -> c.getCourse().getId())
                                        .toList()
                        ))
                        .toList(),
                courseService.getCoursesById(
                        studyPlan.getSections()
                                .stream()
                                .flatMap(sec -> sec.getCourses().stream())
                                .map(c -> c.getCourse().getId())
                                .distinct()
                                .toList()
                )
        );
    }
}
