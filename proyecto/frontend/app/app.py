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
    
    error = None
    form = LoginForm(None if request.method != 'POST' else request.form)

    if not(request.method == "POST"):
        error = 'Mala mia, algo ha fallado'
        return render_template('login.html', form=form,  error=error)
    
    if not sendLogin(form.email.data, form.password.data):
        error = 'Invalid Credentials. Please try again.'
        return render_template('login.html', form=form,  error=error)

    
    userInfo = getUserInfo(form.email.data)
    user = User(1, 
                userInfo['name'], 
                userInfo['email'], 
                userInfo['password'].encode('utf-8'))
    
    login_user(user)
    users.append(user)
    return redirect(url_for('profile'))


@app.route('/signup', methods=['GET', 'POST'])
def signup():
    if request.method == 'GET':
        return render_template('signup.html')
    
    form = SignUpForm(request.form)

    # Enviar solicitud al backend
    name = form.name.data
    email = form.email.data
    passw = form.password.data

    if not sendSignUp(email, name, passw):
        error = "Error al registrarse no signup"
        app.logger.info(error)
        flash(error)
        return render_template('signup.html', form=form, error=error)

    # Mostrar mensaje de éxito
    flash("¡Te has registrado con éxito!")
    return redirect(url_for('login'))


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
    respuesta = getUserInfo(current_user.email)
    return render_template('profile.html', user=respuesta)

@app.route('/bbdd')
@login_required
def bbdd():
    bbdds = getUserInfo(current_user.email)['dbs']
    return render_template('bbdd.html', bbdds=bbdds)

@app.route('/bbdd/<id>')
@login_required
def bbddInfo(id):
    respuesta = getDBInfo(current_user.email,id)
    return render_template('bbddInfo.html', bbdd=respuesta)

@app.route('/bbdd/<id>/eliminar')
@login_required
def bbddEliminar(id):
    bdID = id
    respuesta = removeDB(current_user.email, bdID)
    if respuesta.status_code != 204:
        info = "Error al eliminar la base de datos <"+bdID+">"
    else:
        info = "Base de datos <"+bdID+"> eliminada con éxito"
    flash(info)
    return redirect(url_for('bbdd'))

@app.route('/bbdd/<id>/modificar')
@login_required
def bbddModificar(id):
    respuesta = getDBInfo(current_user.email,id)
    if request.args.__len__() == 0:
        return render_template('bbddModificar.html', bbdd=respuesta)
    
    keys = request.args.getlist("key")
    app.logger.debug(respuesta['d'])
    for key in keys:
        app.logger.debug("Key: "+key)
        # Remove entry ( {key: value}) from d list in response
        for entry in respuesta['d']:
            if entry['k'] == key:
                respuesta['d'].remove(entry)
                break
    app.logger.debug(respuesta)

    # Send request to backend
    respuesta = updateDB(current_user.email, id, respuesta)
    if respuesta.status_code != 204:
        flash("Error al modificar la base de datos")
    else:
        flash("Base de datos modificada con éxito")
    
    return redirect(url_for('bbddModificar', id=id))

@app.route('/bbdd/<bdid>/<key>/eliminar')
@login_required
def bbddEliminarKey(bdid, key):
    respuesta = deletePair(current_user.email, bdid, key)
    if respuesta.status_code != 204:
        flash("Error al eliminar el par <"+key+">")
    else:
        flash("Par <"+key+"> eliminado con éxito")
    return redirect(url_for('bbddModificar', id=bdid))


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

# Con esto enviamos los datos al backend
def sendSignUp(email, name, password):
   
    # Enviar solicitud al backend
    registro = {'email': email, 'name': name, 'password': password}
    try:
        respuesta = requests.post('http://backend-rest:8080/Service/registerUsr', json=registro)
    except requests.exceptions.RequestException as e:
        # Manejo de errores
        app.logger.error("Error sending registration request: " + str(e))
        return False

    # Manejo de respuestas del backend
    if respuesta.status_code == 201:
        return True
    else:
        # Otro error
        flash("Error al registrarse")
    return False


def getUserInfo(email):
    respuesta = requests.get('http://backend-rest:8080/Service/u/'+email)
    if respuesta.status_code == 200:
        return respuesta.json()
    return None

def getDBInfo(email, db):
    respuesta = requests.get('http://backend-rest:8080/Service/u/'+email+'/db/'+db)
    if respuesta.status_code == 200:
        return respuesta.json()
    return None

def removeDB(email, db):
    respuesta = requests.delete('http://backend-rest:8080/Service/u/'+email+'/db/'+db)
    return respuesta

def updateDB(email, db, data):
    respuesta = requests.put('http://backend-rest:8080/Service/u/'+email+'/db/'+db, json=data)
    return respuesta

def deletePair(email, db, key):
    respuesta = requests.delete('http://backend-rest:8080/Service/u/'+email+'/db/'+db+'/d/'+key)
    return respuesta

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
