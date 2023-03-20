from flask_wtf import FlaskForm
from wtforms import (StringField, PasswordField, BooleanField, FileField)
from wtforms.validators import InputRequired, Length, Email

class LoginForm(FlaskForm):
    email = StringField('email', validators=[Email()])
    password = PasswordField('password', validators=[InputRequired()])
    remember_me = BooleanField('remember_me')

class SignUpForm(FlaskForm):
    email = StringField('email', validators=[Email()])
    password = PasswordField('password', validators=[InputRequired()])
    name = StringField('name', validators=[InputRequired(), Length(min=3, max=80)])