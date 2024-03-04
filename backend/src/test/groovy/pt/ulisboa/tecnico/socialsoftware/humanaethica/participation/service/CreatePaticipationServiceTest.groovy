package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser

@DataJpaTest
class CreateParticipationServiceTest extends SpockTest{

    def setup() {

        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        userRepository.save(volunteer)

        def institution = institutionService.getDemoInstitution()

        def theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)

        def themesDto = new ArrayList<>()
        themesDto.add(new ThemeDto(theme,false,false,false))

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                THREE_DAYS_AGO,TWO_DAYS_AGO,ONE_DAY_AGO,themesDto)

        def activity = new Activity(activityDto, institution, [theme] )
        activityRepository.save(activity)
    }

    def "create participation"() {
        given: "an participation dto"

        def participationDto = new ParticipationDto()
        def volunteerDto = new UserDto(volunteer.getAuthUser())
        participationDto.setRating(RATING_1)
        participationDto.setVolunteer(volunteerDto)

        when:
        def result = participationService.createParticipation(activity.getId(), participationDto)

        then: "the returned data is correct"
        result.rating == RATING_1
        result.activity.id == activity.id
        result.volunteer.id == volunteer.id
        and: "the participation is saved in the database"
        participationRepository.findAll().size() == 1
        and: "the stored data is correct"
        def storedParticipation = participationRepository.findById(result.id).get()
        storedParticipation.rating == RATING_1
        storedParticipation.activity.id == activity.id
        storedParticipation.volunteer.id == volunteer.id
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}