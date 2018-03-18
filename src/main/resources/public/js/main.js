var bar = document.getElementById('skill-progress');
var text = document.getElementById('skill-selection');

var skillSlider;

$('#skill-selection-next').on('click', function (e) {
    e.preventDefault();
    nextSkill();
});
$('#skill-selection-previous').on('click', function (e) {
    e.preventDefault();
    previousSkill();
});

function resetTimerSkill() {
    clearInterval(skillSlider);
    skillSlider = setInterval(function () {
        nextSkill();
    }, 3000);
}

function nextSkill() {
    resetTimerSkill()
    text.innerText = skillValues[skillSelection][0];
    bar.value = skillValues[skillSelection][1];
    skillSelection++;

    if ((skillSelection + 1) > skillValues.length) {
        skillSelection = 0;
    }
}

function previousSkill() {
    resetTimerSkill()
    text.innerText = skillValues[skillSelection][0];
    bar.value = skillValues[skillSelection][1];
    skillSelection--;

    if ((skillSelection - 1) < 0) {
        skillSelection = (skillValues.length - 1);
    }
}

var skillSelection = 0;
var skillValues = [
    ["OO-Programming", 93],
    ["Java", 90],
    ["Spring", 80],
    ["SQL", 65],
    ["MongoDB", 80],
    ["Kotlin", 75],
    ["PHP", 65],
    ["HTML5", 99],
    ["CSS3", 90],
    ["Sass", 83],
    ["JavaScript", 63],
    ["Docker", 72],
    ["Neo4J", 60],
    ["Spring", 70],
    ["Dart", 50],
    ["Relational Databases", 65],
    ["Flutter", 50],
    ["Android", 59],
];

$.fn.extend({
    animateCss: function (animationName, callback) {
        var animationEnd = (function (el) {
            var animations = {
                animation: 'animationend',
                OAnimation: 'oAnimationEnd',
                MozAnimation: 'mozAnimationEnd',
                WebkitAnimation: 'webkitAnimationEnd',
            };

            for (var t in animations) {
                if (el.style[t] !== undefined) {
                    return animations[t];
                }
            }
        })(document.createElement('div'));

        this.addClass('animated ' + animationName).one(animationEnd, function () {

            if (typeof callback === 'function') callback();
        });

        return this;
    },
});

$(window).on('load', function () {
    $(".spinner").addClass("hide");

    $('#page').animateCss('fadeIn', function () {
        $(".spinner>div").addClass("off");
        $('#main-box').animateCss('bounce');
    });

});

UIkit.util.ready(function () {
    text.innerText = skillValues[skillSelection][0];
    bar.value = skillValues[skillSelection][1];
    resetTimerSkill()
});

$(document).on('click', 'a', function (e) {
    var loc = $(this).attr("href");

    console.log(loc);
    if (!loc.startsWith("/") || (loc.includes(window.location.hostname) || loc.startsWith("#"))) {
        return;
    }


    window.goto = loc;
    $('#page').animateCss('fadeOut', function () {

        document.location.href = window.goto;
    });


    e.preventDefault();
});


$(document).on('submit', '.ajax-mail-form', function (e) {
    e.preventDefault();
    var url = this.action;
    var form = $(this);

    axios.post(url, form.serialize())
        .then(function (response) {
            console.log(response.da)
            UIkit.notification({message: response.data});
            var button = $("#mail-button");
            button.removeClass("themed-button");
            button.prop('disabled', true);
            button.html("Message Sent!")
        })
        .catch(function (error) {
            console.log(error.response)
            UIkit.notification({message: error.response.data})

        });
});