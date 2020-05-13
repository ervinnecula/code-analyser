
$(document).ready(function(){
    $('#submit-update-config').attr('disabled',true);
    $('#fewCommittersSize').keyup(function(){
        if($(this).val().length !== 0)
            $('#submit-update-config').attr('disabled', false);
        else
            $('#submit-update-config').attr('disabled',true);
    });
    $('#manyCommittersSize').keyup(function(){
        if($(this).val().length !== 0)
            $('#submit-update-config').attr('disabled', false);
        else
            $('#submit-update-config').attr('disabled',true);
    });
    $('#largeFileSize').keyup(function(){
        if($(this).val().length !== 0)
            $('#submit-update-config').attr('disabled', false);
        else
            $('#submit-update-config').attr('disabled',true);
    });
    $('#hugeFileSize').keyup(function(){
        if($(this).val().length !== 0)
            $('#submit-update-config').attr('disabled', false);
        else
            $('#submit-update-config').attr('disabled',true);
    });
    $('#mediumChangeSize').keyup(function(){
        if($(this).val().length !== 0)
            $('#submit-update-config').attr('disabled', false);
        else
            $('#submit-update-config').attr('disabled',true);
    });
    $('#majorChangeSize').keyup(function(){
        if($(this).val().length !== 0)
            $('#submit-update-config').attr('disabled', false);
        else
            $('#submit-update-config').attr('disabled',true);
    });
    $('#periodOfTimeSize').keyup(function(){
        if($(this).val().length !== 0)
            $('#submit-update-config').attr('disabled', false);
        else
            $('#submit-update-config').attr('disabled',true);
    });
});