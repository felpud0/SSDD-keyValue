from flask import Flask, render_template, send_from_directory, url_for, request, redirect, flash
from flask_login import LoginManager, login_manager, current_user, login_user, login_required, logout_user
import requests
import os

# Usuarios
from models import users, User

# Login
from forms import LoginForm, SignUpForm

app = Flask(__name__, static_url_path='')
login_manager = LoginManager()
login_manager.init_app(app) # Para mantener la sesión

# Configurar el secret_key. OJO, no debe ir en un servidor git público.
# Python ofrece varias formas de almacenar esto de forma segura, que
# no cubriremos aquí.
app.config['SECRET_KEY'] = 'qH1vprMjavek52cv7Lmfe1FoCexrrV8egFnB21jHhkuOHm8hJUe1hwn7pKEZQ1fioUzDb3sWcNK1pJVVIhyrgvFiIrceXpKJBFIn_i9-LTLBCc4cqaI3gjJJHU6kxuT8bnC7Ng'

@app.route('/static/<path:path>')
def serve_static(path):
    return send_from_directory('static', path)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    else:
        error = None
        form = LoginForm(None if request.method != 'POST' else request.form)
        if request.method == "POST" and form.validate():
            if not sendLogin(form.email.data, form.password.data):
                error = 'Invalid Credentials. Please try again.'

            else:
                userInfo = getUserInfo(form.email.data)
                user = User(1, 
                            userInfo['name'], 
                            userInfo['email'], 
                            userInfo['password'].encode('utf-8'))
                login_user(user)
                users.append(user)
                return redirect(url_for('profile'))

    return render_template('login.html', form=form,  error=error)

@app.route('/signup', methods=['GET', 'POST'])
def signup():

    if request.method == 'GET':
        return render_template('signup.html')
    
    error = None
    form = SignUpForm(request.form)
    if not form.validate_on_submit():
        error = 'Invalid Credentials. Please try again.'
        
    # TODO 1: Añadir el usuario a la BD? 
    name = form.name.data.encode('utf-8')
    email = form.email.data.encode('utf-8')
    passw = form.password.data.encode('utf-8')
    user = User(2, name , email, passw)

    return "Registrado " + str(user.name) + " " + str(user.email)
def flash_errors(form):
    """Flashes form errors"""
    for field, errors in form.errors.items():
        for error in errors:
            flash(u"Error in the %s field - %s" % (
                getattr(form, field).label.text,
                error
            ), 'error')

@app.route('/profile')
@login_required
def profile():
    return render_template('profile.html')

@app.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('index'))

@login_manager.user_loader
def load_user(user_id):
    for user in users:
        if user.id == int(user_id):
            return user
    return None

def sendLogin(email, password):
    login = {'email': email, 'password': password}
    # Por defecto se crea una red con un servicio DNS, donde el nombre
    # de cada servidor es el nombre de cada servicio (diapositiva 38 - docker)
    respuesta = requests.post('http://backend-rest:8080/Service/checkLogin', json=login)
    flash(respuesta.status_code)
    if respuesta.status_code == 200:
        return True
    return False

def getUserInfo(email):
    respuesta = requests.get('http://backend-rest:8080/Service/u/'+email)
    if respuesta.status_code == 200:
        return respuesta.json()
    return None

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
