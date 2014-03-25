var personRepository = sdr.repositoriesFactory(null).personRepository;

function deletePic(id) {
    personRepository.delete(id).done(function(){
        window.location.reload();
    });
}

$("#add").submit(function(event){
    event.preventDefault();
    var $form = $(this),
        firstName = $form.find('input[name="firstName"]').val(),
        lastName = $form.find('input[name="lastName"]').val();
    personRepository.create({
        firstName: firstName,
        lastName: lastName
    }).done(function(){
        window.location.reload();
    });
});
